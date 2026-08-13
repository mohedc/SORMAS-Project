/**
 * Replace Highcharts Cloud export with a local DOM screenshot.
 * Must load after highcharts-exporting.js and before welhtmltoimagedownloader.js is used.
 */
(function (H) {
	if (!H || !H.setOptions) {
		return;
	}

	H.setOptions({
		exporting: {
			fallbackToExportServer: false,
			menuItemDefinitions: {
				downloadPNG: {
					textKey: "downloadPNG",
					onclick: function () {
						window.downloadHtmlAsImage("image/png", "chart", this.renderTo && this.renderTo.id);
					}
				},
				downloadJPEG: {
					textKey: "downloadJPEG",
					onclick: function () {
						window.downloadHtmlAsImage("image/jpeg", "chart", this.renderTo && this.renderTo.id);
					}
				},
				downloadPDF: {
					textKey: "downloadPDF",
					onclick: function () {
						window.downloadPDF("downloaded-pdf", this.renderTo && this.renderTo.id);
					}
				},
				downloadSVG: {
					textKey: "downloadSVG",
					onclick: function () {
						window.downloadHtmlAsImage("image/svg+xml", "chart", this.renderTo && this.renderTo.id);
					}
				}
			}
		}
	});
}(window.Highcharts));
