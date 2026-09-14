/*
 * SORMAS® - Surveillance Outbreak Response Management & Analysis System
 * Copyright © 2016-2018 Helmholtz-Zentrum für Infektionsforschung GmbH (HZI)
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <https://www.gnu.org/licenses/>.
 */

package de.symeda.sormas.app.util;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Criteria;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Looper;
import android.util.Log;

import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import org.apache.commons.lang3.StringUtils;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

import de.symeda.sormas.app.R;
import de.symeda.sormas.app.backend.common.DatabaseHelper;
import de.symeda.sormas.app.backend.config.ConfigProvider;

/**
 * Created by Mate Strysewske on 16.08.2017.
 *
 * See https://developer.android.com/guide/topics/location/strategies.html for a guide
 */
public final class LocationService {

	private static LocationService instance;

	public static LocationService instance() {
		return instance;
	}

	private Context context;
	private Location bestKnownLocation = null;
	private AlertDialog requestGpsAccessDialog;
	private Geocoder geocoder;

	private LocationService() {
	}

	/**
	 * Creates a base request for GPS and network every 10 minutes when more than 100m moved
	 */
	public static void init(Context context) {
		if (instance != null) {
			throw new UnsupportedOperationException("instance already initialized");
		}
		instance = new LocationService();
		instance.context = context;

		instance.geocoder = new Geocoder(context, Locale.getDefault());

		LocationManager locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);

		if (ContextCompat.checkSelfPermission(context, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
			return;
		}

		try {
			if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
				return;
			}

			// every 20 minutes
			LocationListener gpsBaseLocationListener = new BestKnownLocationListener();
			locationManager
				.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000 * 60 * 20, 1000, gpsBaseLocationListener, Looper.getMainLooper());

			LocationListener networkBaseLocationListener = new BestKnownLocationListener();
			locationManager
				.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 1000 * 60 * 20, 1000, networkBaseLocationListener, Looper.getMainLooper());

		} catch (SecurityException e) {
			Log.e(LocationService.class.getName(), "Error while initializing LocationService", e);
		}
	}

	int activeRequestCount = 0;
	LocationListener gpsActiveLocationListener = null;
	LocationListener networkActiveLocationListener = null;

	/**
	 * Request network and GPS once a minute
	 */
	public void requestActiveLocationUpdates(Activity callingActivity) {

		activeRequestCount++;

		if (ConfigProvider.getUser() == null || !validateGpsAccessAndEnabled(callingActivity)) {
			return;
		}

		if (!isRequestingActiveLocationUpdates()) {

			if (ContextCompat.checkSelfPermission(context, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
				return;
			}

			LocationManager locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);

			networkActiveLocationListener = new BestKnownLocationListener();
			locationManager
				.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 1000 * 60, 10, networkActiveLocationListener, Looper.getMainLooper());
			gpsActiveLocationListener = new BestKnownLocationListener();
			locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000 * 60, 10, gpsActiveLocationListener, Looper.getMainLooper());
		}
	}

	public void removeActiveLocationUpdates() {

		if (activeRequestCount < 1) {
			throw new UnsupportedOperationException("removeActiveLocationUpdates was called although no active request exists");
		}

		activeRequestCount--;

		if (activeRequestCount == 0 && isRequestingActiveLocationUpdates()) {

			LocationManager locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);

			locationManager.removeUpdates(networkActiveLocationListener);
			networkActiveLocationListener = null;
			locationManager.removeUpdates(gpsActiveLocationListener);
			gpsActiveLocationListener = null;
		}
	}

	public boolean isRequestingActiveLocationUpdates() {
		return gpsActiveLocationListener != null;
	}

	public void requestFreshLocation(Activity callingActivity) {

		if (ConfigProvider.getUser() == null || !validateGpsAccessAndEnabled(callingActivity)) {
			return;
		}

		// just for compiler reasons
		if (ContextCompat.checkSelfPermission(context, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
			return;
		}

		LocationManager locationManager = (LocationManager) context.getSystemService(context.LOCATION_SERVICE);
		locationManager.requestSingleUpdate(LocationManager.GPS_PROVIDER, new BestKnownLocationListener(), Looper.getMainLooper());
		locationManager.requestSingleUpdate(LocationManager.NETWORK_PROVIDER, new BestKnownLocationListener(), Looper.getMainLooper());
	}

	/**
	 * Last known GPS/network location without country filtering.
	 * Use when the user explicitly picks their current position for an address.
	 */
	public Location getLastKnownLocationRaw() {
		if (!hasGpsAccess()) {
			return null;
		}

		LocationManager locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
		Location gpsLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
		Location networkLocation = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);

		if (gpsLocation == null) {
			return networkLocation;
		}
		if (networkLocation == null) {
			return gpsLocation;
		}

		// Prefer the more recent reading; if equally recent, prefer the more accurate one
		long timeDelta = gpsLocation.getTime() - networkLocation.getTime();
		if (timeDelta > SIGNIFICANTLY_NEWER_DELTA) {
			return gpsLocation;
		}
		if (timeDelta < -SIGNIFICANTLY_NEWER_DELTA) {
			return networkLocation;
		}
		return gpsLocation.getAccuracy() <= networkLocation.getAccuracy() ? gpsLocation : networkLocation;
	}

	public static final float MIN_GPS_ACCURACY_METERS = 1f;
	public static final float MAX_GPS_ACCURACY_METERS = 5f;

	/**
	 * GPS accuracy (m) must be between 1 and 5 inclusive.
	 */
	public static boolean isGpsAccuracyAcceptable(float accuracyMeters) {
		return accuracyMeters >= MIN_GPS_ACCURACY_METERS && accuracyMeters <= MAX_GPS_ACCURACY_METERS;
	}

	/**
	 * Requests continuous high-accuracy GPS updates until a fix with accuracy between 1 and 5 meters
	 * is available, or the timeout elapses. Prefers the GPS provider (not network) to improve accuracy.
	 * On timeout, returns the best location obtained so far (even if accuracy is outside 1–5 m),
	 * or null if no fix was received.
	 */
	public void requestAccurateCurrentLocation(Activity callingActivity, long timeoutMillis, java.util.function.Consumer<Location> callback) {
		if (!hasGpsAccess() || !hasGpsEnabled()) {
			callback.accept(null);
			return;
		}

		LocationManager locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
		final boolean[] delivered = {
			false };
		final Location[] bestLocation = {
			null };

		// Accept a recent cached GPS fix only if it already meets the accuracy requirement
		Location cachedGps = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
		if (cachedGps != null && isGpsAccuracyAcceptable(cachedGps.getAccuracy())) {
			bestKnownLocation = cachedGps;
			callback.accept(cachedGps);
			return;
		}
		if (cachedGps != null) {
			bestLocation[0] = cachedGps;
		}

		LocationListener listener = new LocationListener() {

			@Override
			public void onLocationChanged(Location location) {
				if (delivered[0] || location == null || !location.hasAccuracy()) {
					return;
				}

				if (bestLocation[0] == null || location.getAccuracy() < bestLocation[0].getAccuracy()) {
					bestLocation[0] = location;
				}

				if (!isGpsAccuracyAcceptable(location.getAccuracy())) {
					return;
				}

				delivered[0] = true;
				bestKnownLocation = location;
				try {
					locationManager.removeUpdates(this);
				} catch (Exception ignored) {
				}
				callback.accept(location);
			}

			@Override
			public void onStatusChanged(String provider, int status, Bundle extras) {
			}

			@Override
			public void onProviderEnabled(String provider) {
			}

			@Override
			public void onProviderDisabled(String provider) {
			}
		};

		try {
			// Continuous fine GPS updates so accuracy can improve over a few seconds
			if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
				locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 500, 0, listener, Looper.getMainLooper());
			}

			Criteria criteria = new Criteria();
			criteria.setAccuracy(Criteria.ACCURACY_FINE);
			criteria.setPowerRequirement(Criteria.POWER_HIGH);
			criteria.setAltitudeRequired(false);
			criteria.setBearingRequired(false);
			criteria.setSpeedRequired(false);
			String bestProvider = locationManager.getBestProvider(criteria, true);
			if (bestProvider != null
				&& !LocationManager.GPS_PROVIDER.equals(bestProvider)
				&& !LocationManager.NETWORK_PROVIDER.equals(bestProvider)) {
				locationManager.requestLocationUpdates(bestProvider, 500, 0, listener, Looper.getMainLooper());
			}
		} catch (SecurityException | IllegalArgumentException e) {
			Log.e(LocationService.class.getName(), "Error while requesting accurate location", e);
			callback.accept(null);
			return;
		}

		new android.os.Handler(Looper.getMainLooper()).postDelayed(() -> {
			if (delivered[0]) {
				return;
			}
			delivered[0] = true;
			try {
				locationManager.removeUpdates(listener);
			} catch (Exception ignored) {
			}

			Location best = bestLocation[0];
			if (best != null) {
				bestKnownLocation = best;
				callback.accept(best);
			} else {
				callback.accept(null);
			}
		}, timeoutMillis);
	}

	/**
	 * Requests a single fresh location update and delivers it to the callback (without country filtering).
	 * Invokes the callback with null if no fix arrives within the timeout.
	 */
	public void requestSingleCurrentLocation(Activity callingActivity, long timeoutMillis, java.util.function.Consumer<Location> callback) {
		if (!hasGpsAccess()) {
			callback.accept(null);
			return;
		}

		LocationManager locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
		final boolean[] delivered = {
			false };

		LocationListener listener = new LocationListener() {

			@Override
			public void onLocationChanged(Location location) {
				if (delivered[0] || location == null) {
					return;
				}
				delivered[0] = true;
				bestKnownLocation = location;
				try {
					locationManager.removeUpdates(this);
				} catch (Exception ignored) {
				}
				callback.accept(location);
			}

			@Override
			public void onStatusChanged(String provider, int status, Bundle extras) {
			}

			@Override
			public void onProviderEnabled(String provider) {
			}

			@Override
			public void onProviderDisabled(String provider) {
			}
		};

		try {
			if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
				locationManager.requestSingleUpdate(LocationManager.GPS_PROVIDER, listener, Looper.getMainLooper());
			}
			if (locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) {
				locationManager.requestSingleUpdate(LocationManager.NETWORK_PROVIDER, listener, Looper.getMainLooper());
			}
		} catch (SecurityException | IllegalArgumentException e) {
			Log.e(LocationService.class.getName(), "Error while requesting current location", e);
			callback.accept(null);
			return;
		}

		new android.os.Handler(Looper.getMainLooper()).postDelayed(() -> {
			if (delivered[0]) {
				return;
			}
			delivered[0] = true;
			try {
				locationManager.removeUpdates(listener);
			} catch (Exception ignored) {
			}
			callback.accept(getLastKnownLocationRaw());
		}, timeoutMillis);
	}

	public static final class BestKnownLocationListener implements LocationListener {

		@Override
		public void onLocationChanged(Location location) {
			if (isBetterLocation(location, instance.bestKnownLocation)) {
				instance.bestKnownLocation = location;
			}
		}

		@Override
		public void onStatusChanged(String s, int i, Bundle bundle) {

		}

		@Override
		public void onProviderEnabled(String s) {

		}

		@Override
		public void onProviderDisabled(String s) {
			// TODO should we do something about this?
		}
	}

	public Location getLocation(Activity callingActivity) {

		if (ConfigProvider.getUser() == null || !ensureGpsAccessAndEnabled(callingActivity)) {
			return null;
		}

		return getLocation();
	}

	public Location getLocation() {

		// just for compiler reasons
		if (ContextCompat.checkSelfPermission(context, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
			return null;
		}

		LocationManager locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);

		Location location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
		if (isBetterLocation(location, bestKnownLocation)) {
			bestKnownLocation = location;
		} else {
			location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
			if (isBetterLocation(location, bestKnownLocation)) {
				bestKnownLocation = location;
			}
		}

		return bestKnownLocation;
	}

	private static final int SIGNIFICANTLY_NEWER_DELTA = 1000 * 60 * 2;

	/**
	 * Determines whether one Location reading is better than the current Location fix
	 * 
	 * @param location
	 *            The new Location that you want to evaluate
	 * @param currentBestLocation
	 *            The current Location fix, to which you want to compare the new one
	 */
	protected static boolean isBetterLocation(Location location, Location currentBestLocation) {
		if (location == null) {
			return false;
		}
		// Discard the location if it's not in the server's country
		String countryLocale = ConfigProvider.getServerLocale();
		if (!StringUtils.isEmpty(countryLocale)) {
			try {
				List<Address> addresses = instance.geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
				if (addresses.size() > 0) {
					String countryCode = addresses.get(0).getCountryCode();
					if (countryCode == null || !countryCode.equals(countryLocale.substring(countryLocale.indexOf("-") + 1).toUpperCase())) {
						return false;
					}
				}
			} catch (IOException e) {
				// If retrieving the country of the location fails, assume it is valid
			}
		}
		// A new location is better than no location
		if (currentBestLocation == null) {
			return true;
		}

		// Check whether the new location fix is newer or older
		long timeDelta = location.getTime() - currentBestLocation.getTime();
		boolean isSignificantlyNewer = timeDelta > SIGNIFICANTLY_NEWER_DELTA;
		boolean isSignificantlyOlder = timeDelta < -SIGNIFICANTLY_NEWER_DELTA;
		boolean isNewer = timeDelta > 0;

		// If it's been more than two minutes since the current location, use the new location
		// because the user has likely moved
		if (isSignificantlyNewer) {
			return true;
			// If the new location is more than two minutes older, it must be worse
		} else if (isSignificantlyOlder) {
			return false;
		}

		// Check whether the new location fix is more or less accurate
		int accuracyDelta = (int) (location.getAccuracy() - currentBestLocation.getAccuracy());
		boolean isLessAccurate = accuracyDelta > 0;
		boolean isMoreAccurate = accuracyDelta < 0;
		boolean isSignificantlyLessAccurate = accuracyDelta > 200;

		// Check if the old and new location are from the same provider
		boolean isFromSameProvider = isSameProvider(location.getProvider(), currentBestLocation.getProvider());

		// Determine location quality using a combination of timeliness and accuracy
		if (isMoreAccurate) {
			return true;
		} else if (isNewer && !isLessAccurate) {
			return true;
		} else if (isNewer && !isSignificantlyLessAccurate && isFromSameProvider) {
			return true;
		}
		return false;
	}

	/** Checks whether two providers are the same */
	private static boolean isSameProvider(String provider1, String provider2) {
		if (provider1 == null) {
			return provider2 == null;
		}
		return provider1.equals(provider2);
	}

	/**
	 * Checks whether the SORMAS app has the permission to access the phone's location
	 */
	public boolean hasGpsAccess() {
		return ContextCompat.checkSelfPermission(context, android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED;
	}

	/**
	 * Checks whether the phone's GPS is turned on
	 * 
	 * @return
	 */
	public boolean hasGpsEnabled() {
		LocationManager locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
		return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
	}

	public boolean validateGpsAccessAndEnabled(final Activity callingActivity) {

		if (DatabaseHelper.getFeatureConfigurationDao().isAnySurveillanceEnabled()) {
			return ensureGpsAccessAndEnabled(callingActivity);
		}

		return true;
	}

	/**
	 * Ensures the app has location permission and that GPS is turned on.
	 * Shows the permission or GPS-enable dialog when needed.
	 * Always runs when the user explicitly picks GPS (not gated by feature flags).
	 *
	 * @return true if GPS can be used right now
	 */
	public boolean ensureGpsAccessAndEnabled(final Activity callingActivity) {
		if (!hasGpsAccess()) {
			buildAndShowRequestGpsAccessDialog(callingActivity);
			return false;
		}

		if (!hasGpsEnabled()) {
			AlertDialog turnOnGPSDialog = buildEnableGpsDialog(callingActivity);
			turnOnGPSDialog.show();
			return false;
		}

		return true;
	}

	/**
	 * Shows a dialog to request GPS access from the user and makes sure that this dialog is only
	 * displayed once.
	 * 
	 * @param callingActivity
	 */
	private void buildAndShowRequestGpsAccessDialog(final Activity callingActivity) {
		if (hasGpsAccess()) {
			return;
		}
		if (requestGpsAccessDialog != null && requestGpsAccessDialog.isShowing()) {
			return;
		}

		AlertDialog.Builder builder = new AlertDialog.Builder(callingActivity);
		builder.setCancelable(false);
		builder.setMessage(R.string.message_gps_permission);
		builder.setTitle(R.string.heading_gps_permission);
		builder.setIcon(R.drawable.ic_perm_device_information_black_24dp);
		requestGpsAccessDialog = builder.create();

		requestGpsAccessDialog
			.setButton(AlertDialog.BUTTON_POSITIVE, callingActivity.getString(R.string.action_close_app), new DialogInterface.OnClickListener() {

				@Override
				public void onClick(DialogInterface dialog, int which) {
					requestGpsAccessDialog = null;
					Activity finishActivity = callingActivity;
					do {
						finishActivity.finish();
						finishActivity = finishActivity.getParent();
					}
					while (finishActivity != null);
				}
			});
		requestGpsAccessDialog.setButton(
			AlertDialog.BUTTON_NEGATIVE,
			callingActivity.getString(R.string.action_allow_gps_access),
			new DialogInterface.OnClickListener() {

				@Override
				public void onClick(DialogInterface dialog, int which) {
					ActivityCompat.requestPermissions(
						callingActivity,
						new String[] {
							Manifest.permission.ACCESS_FINE_LOCATION },
						9999);
					requestGpsAccessDialog = null;
				}
			});

		requestGpsAccessDialog.show();
	}

	public AlertDialog buildEnableGpsDialog(final Activity callingActivity) {
		AlertDialog.Builder builder = new AlertDialog.Builder(callingActivity);
		builder.setCancelable(false);
		builder.setMessage(R.string.message_gps_activation);
		builder.setTitle(R.string.heading_gps_activation);
		builder.setIcon(R.drawable.ic_location_on_black_24dp);
		AlertDialog dialog = builder.create();
		dialog.setButton(AlertDialog.BUTTON_POSITIVE, callingActivity.getString(R.string.action_close_app), new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				Activity finishActivity = callingActivity;
				do {
					finishActivity.finish();
					finishActivity = finishActivity.getParent();
				}
				while (finishActivity != null);
			}
		});
		dialog.setButton(AlertDialog.BUTTON_NEGATIVE, callingActivity.getString(R.string.action_turn_on_gps), new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				Intent gpsOptionsIntent = new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS);
				callingActivity.startActivity(gpsOptionsIntent);
			}
		});

		return dialog;
	}
}
