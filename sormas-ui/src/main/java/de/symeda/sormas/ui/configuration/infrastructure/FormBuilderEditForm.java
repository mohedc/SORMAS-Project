/*
 * SORMAS® - Surveillance Outbreak Response Management & Analysis System
 * Copyright © 2016-2022 Helmholtz-Zentrum für Infektionsforschung GmbH (HZI)
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

package de.symeda.sormas.ui.configuration.infrastructure;

import static de.symeda.sormas.ui.utils.LayoutUtil.fluidRowLocs;
import static de.symeda.sormas.ui.utils.LayoutUtil.loc;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import com.vaadin.ui.Button;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.v7.data.Item;
import com.vaadin.v7.data.util.IndexedContainer;
import com.vaadin.v7.ui.ComboBox;
import com.vaadin.v7.ui.Table;
import com.vaadin.v7.ui.TextField;

import de.symeda.sormas.api.Disease;
import de.symeda.sormas.api.EntityRelevanceStatus;
import de.symeda.sormas.api.FacadeProvider;
import de.symeda.sormas.api.FormType;
import de.symeda.sormas.api.i18n.Captions;
import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.infrastructure.fields.FormFieldIndexDto;
import de.symeda.sormas.api.infrastructure.fields.FormFieldReferenceDto;
import de.symeda.sormas.api.infrastructure.fields.FormFieldsCriteria;
import de.symeda.sormas.api.infrastructure.forms.FormBuilderDto;
import de.symeda.sormas.api.utils.SortProperty;
import de.symeda.sormas.ui.utils.AbstractEditForm;
import de.symeda.sormas.ui.utils.FieldHelper;

public class FormBuilderEditForm extends AbstractEditForm<FormBuilderDto> {

	private static final long serialVersionUID = 1L;

	private static final String FIELDS_SELECTION_LOCATION = "searchFieldsLocation";
	private static final String HTML_LAYOUT = fluidRowLocs(FormBuilderDto.DISEASE, FormBuilderDto.FORM_TYPE)
		+ loc(FIELDS_SELECTION_LOCATION);

	private static final String PROPERTY_FIELD = "field";
	private static final String PROPERTY_ORDER = "order";
	private static final String PROPERTY_NAME = "name";

	private boolean create;
	private ComboBox disease;
	private ComboBox formType;
	private Table availableFieldsTable;
	private Table selectedFieldsTable;
	private IndexedContainer availableFieldsContainer;
	private IndexedContainer selectedFieldsContainer;
	private FormFieldsCriteria criteria;
	private int currentOrder = 0;
	private boolean firstPageLoad = true;
	private Set<String> lastSelectedIds;
	
	// Search and counter components
	private TextField availableFieldsSearch;
	private TextField selectedFieldsSearch;
	private Label availableFieldsCountLabel;
	private Label selectedFieldsCountLabel;
	private IndexedContainer availableFieldsFullContainer; // Store all available fields for filtering

	public FormBuilderEditForm(boolean create) {
		super(FormBuilderDto.class, FormBuilderDto.I18N_PREFIX, false);
		this.create = create;

		setWidth(100, Unit.PERCENTAGE);
		setHeight(700, Unit.PIXELS);

		if (create) {
			hideValidationUntilNextCommit();
		}

		criteria = new FormFieldsCriteria();
		// Set default to show only active fields
		criteria.relevanceStatus(EntityRelevanceStatus.ACTIVE);
		addFields();
	}

	@Override
	protected void addFields() {
		disease = addField(FormBuilderDto.DISEASE, ComboBox.class);
		disease.addItems(Disease.values());
		disease.setNullSelectionAllowed(false);

		formType = addField(FormBuilderDto.FORM_TYPE, ComboBox.class);
		formType.addItems(FormType.values());
		formType.setNullSelectionAllowed(false);

		// Available Fields Search and Counter
		VerticalLayout availableFieldsLayout = new VerticalLayout();
		availableFieldsLayout.setSpacing(true);
		availableFieldsLayout.setMargin(false);
		availableFieldsLayout.setWidth("100%");
		availableFieldsLayout.setSizeFull();
		
		availableFieldsSearch = new TextField();
		availableFieldsSearch.setWidth("100%");
		availableFieldsSearch.setSizeUndefined();
		availableFieldsSearch.setInputPrompt("Search available fields...");
		availableFieldsSearch.setNullRepresentation("");
		availableFieldsSearch.setImmediate(true);
		availableFieldsSearch.addTextChangeListener(e -> filterAvailableFields(e.getText()));
		
		availableFieldsCountLabel = new Label();
		availableFieldsCountLabel.setValue("Available: 0");
		availableFieldsCountLabel.addStyleName("v-label-small");
		availableFieldsCountLabel.setWidth("100%");
		
		// Available Fields (Table)
		availableFieldsTable = new Table();
		availableFieldsTable.setWidth("100%");
		availableFieldsTable.setHeight("600px");
		availableFieldsTable.setCaption(I18nProperties.getCaption(Captions.formFieldsAvailable));
		availableFieldsTable.setSelectable(true);
		availableFieldsTable.setMultiSelect(true);

		availableFieldsContainer = new IndexedContainer();
		availableFieldsContainer.addContainerProperty(PROPERTY_FIELD, FormFieldIndexDto.class, null);
		availableFieldsContainer.addContainerProperty(PROPERTY_NAME, String.class, null);
		availableFieldsTable.setContainerDataSource(availableFieldsContainer);

		availableFieldsTable.setColumnHeader(PROPERTY_NAME, "Description");
		availableFieldsTable.setVisibleColumns(PROPERTY_NAME);
		
		availableFieldsLayout.addComponent(availableFieldsCountLabel);
		availableFieldsLayout.addComponent(availableFieldsSearch);
		availableFieldsLayout.addComponent(availableFieldsTable);
		availableFieldsLayout.setExpandRatio(availableFieldsTable, 1);

		// Selected Fields Search and Counter
		VerticalLayout selectedFieldsLayout = new VerticalLayout();
		selectedFieldsLayout.setSpacing(true);
		selectedFieldsLayout.setMargin(false);
		selectedFieldsLayout.setWidth("100%");
		selectedFieldsLayout.setSizeFull();
		
		selectedFieldsSearch = new TextField();
		selectedFieldsSearch.setWidth("100%");
		selectedFieldsSearch.setSizeUndefined();
		selectedFieldsSearch.setInputPrompt("Search selected fields...");
		selectedFieldsSearch.setNullRepresentation("");
		selectedFieldsSearch.setImmediate(true);
		selectedFieldsSearch.addTextChangeListener(e -> filterSelectedFields(e.getText()));
		
		selectedFieldsCountLabel = new Label();
		selectedFieldsCountLabel.setValue("Selected: 0");
		selectedFieldsCountLabel.addStyleName("v-label-small");
		selectedFieldsCountLabel.setWidth("100%");

		// Selected Fields (Table)
		selectedFieldsTable = new Table();
		selectedFieldsTable.setWidth("100%");
		selectedFieldsTable.setHeight("600px");
		selectedFieldsTable.setCaption(I18nProperties.getCaption(Captions.formFieldsSelected));
		selectedFieldsTable.setSelectable(true);
		selectedFieldsTable.setMultiSelect(true);

		selectedFieldsContainer = new IndexedContainer();
		selectedFieldsContainer.addContainerProperty(PROPERTY_FIELD, FormFieldIndexDto.class, null);
		selectedFieldsContainer.addContainerProperty(PROPERTY_ORDER, Integer.class, null);
		selectedFieldsContainer.addContainerProperty(PROPERTY_NAME, String.class, null);
		selectedFieldsTable.setContainerDataSource(selectedFieldsContainer);

		selectedFieldsTable.setColumnHeader(PROPERTY_ORDER, "Order");
		selectedFieldsTable.setColumnHeader(PROPERTY_NAME, "Description");
		selectedFieldsTable.setVisibleColumns(PROPERTY_ORDER, PROPERTY_NAME);
		selectedFieldsTable.setColumnAlignment(PROPERTY_ORDER, Table.Align.CENTER);
		
		selectedFieldsLayout.addComponent(selectedFieldsCountLabel);
		selectedFieldsLayout.addComponent(selectedFieldsSearch);
		selectedFieldsLayout.addComponent(selectedFieldsTable);
		selectedFieldsLayout.setExpandRatio(selectedFieldsTable, 1);

		// Action Buttons
		Button addButton = new Button("→");
		Button removeButton = new Button("←");
		Button moveUpButton = new Button(" ↑ ");
		Button moveDownButton = new Button(" ↓ ");
		Button moveToTopButton = new Button("↑↑");

		VerticalLayout buttonLayout = new VerticalLayout();
		buttonLayout.setSpacing(true);
		buttonLayout.addComponents(addButton, removeButton, moveUpButton, moveDownButton, moveToTopButton);

		// Add button action
		addButton.addClickListener(event -> {
			@SuppressWarnings("unchecked")
			Set<String> selectedIds = (Set<String>) availableFieldsTable.getValue();
			if (selectedIds != null && !selectedIds.isEmpty()) {
				moveFieldsToSelected(selectedIds);
			}
		});

		// Remove button action
		removeButton.addClickListener(event -> {
			@SuppressWarnings("unchecked")
			Set<String> selectedIds = (Set<String>) selectedFieldsTable.getValue();
			if (selectedIds != null && !selectedIds.isEmpty()) {
				moveFieldsToAvailable(selectedIds);
			}
		});

		// Double-click handler for available fields table - move to selected
		availableFieldsTable.addItemClickListener(event -> {
			if (event.isDoubleClick()) {
				Object itemId = event.getItemId();
				if (itemId != null) {
					Set<String> itemIdSet = new HashSet<>();
					itemIdSet.add(itemId.toString());
					moveFieldsToSelected(itemIdSet);
				}
			}
		});

		// Double-click handler for selected fields table - move to available
		selectedFieldsTable.addItemClickListener(event -> {
			if (event.isDoubleClick()) {
				Object itemId = event.getItemId();
				if (itemId != null) {
					Set<String> itemIdSet = new HashSet<>();
					itemIdSet.add(itemId.toString());
					moveFieldsToAvailable(itemIdSet);
				}
			}
		});

		// Move up button action
		moveUpButton.addClickListener(event -> {
			@SuppressWarnings("unchecked")
			Set<String> selectedIds = (Set<String>) selectedFieldsTable.getValue();
			if (selectedIds != null && !selectedIds.isEmpty()) {
				lastSelectedIds = new HashSet<>(selectedIds);
				for (String id : selectedIds) {
					moveItem(id, true);
				}
				updateFormFieldsList();
				// Re-apply selected fields filter
				if (selectedFieldsSearch != null) {
					filterSelectedFields(selectedFieldsSearch.getValue());
				}
				// Restore selection after filtering
				selectedFieldsTable.setValue(lastSelectedIds);
			}
		});

		// Move down button action
		moveDownButton.addClickListener(event -> {
			@SuppressWarnings("unchecked")
			Set<String> selectedIds = (Set<String>) selectedFieldsTable.getValue();
			if (selectedIds != null && !selectedIds.isEmpty()) {
				lastSelectedIds = new HashSet<>(selectedIds);
				Object[] selectedIdsArray = selectedIds.toArray();
				// Reverse order for move down to maintain visual order
				for (int i = selectedIdsArray.length - 1; i >= 0; i--) {
					moveItem((String) selectedIdsArray[i], false);
				}
				updateFormFieldsList();
				// Re-apply selected fields filter
				if (selectedFieldsSearch != null) {
					filterSelectedFields(selectedFieldsSearch.getValue());
				}
				// Restore selection after filtering
				selectedFieldsTable.setValue(lastSelectedIds);
			}
		});

		// Move to top button action
		moveToTopButton.addClickListener(event -> {
			@SuppressWarnings("unchecked")
			Set<String> selectedIds = (Set<String>) selectedFieldsTable.getValue();
			if (selectedIds != null && !selectedIds.isEmpty()) {
				lastSelectedIds = new HashSet<>(selectedIds);
				List<String> selectedIdsList = new ArrayList<>(selectedIds);
				Collections.reverse(selectedIdsList);
				for (String id : selectedIdsList) {
					moveItemToTop(id);
				}
				updateFormFieldsList();
				// Re-apply selected fields filter (moveItemToTop already calls it, but we need to restore selection after)
				if (selectedFieldsSearch != null) {
					filterSelectedFields(selectedFieldsSearch.getValue());
				}
				// Restore selection after filtering
				selectedFieldsTable.setValue(lastSelectedIds);
			}
		});

		// Layout assembly
		HorizontalLayout fieldSelectionLayout = new HorizontalLayout();
		fieldSelectionLayout.setWidth("100%");
		fieldSelectionLayout.setSpacing(true);
		fieldSelectionLayout.addComponents(availableFieldsLayout, buttonLayout, selectedFieldsLayout);
		fieldSelectionLayout.setExpandRatio(availableFieldsLayout, 1.0f);
		fieldSelectionLayout.setExpandRatio(selectedFieldsLayout, 1.0f);
		getContent().addComponent(fieldSelectionLayout, FIELDS_SELECTION_LOCATION);

		// FormType change handler
		formType.addValueChangeListener(event -> {
			FormType selectedFormType = (FormType) event.getProperty().getValue();
			if (selectedFormType != null) {
				FormType previousFormType = criteria.getFormType();
				criteria.setFormType(selectedFormType);
				// Ensure relevanceStatus is set to ACTIVE
				if (criteria.getRelevanceStatus() == null) {
					criteria.relevanceStatus(EntityRelevanceStatus.ACTIVE);
				}
				// Only clear fields if formType actually changed (not on initial load)
				if (previousFormType != null && !previousFormType.equals(selectedFormType)) {
					// User changed formType - clear selected fields
					if (getValue() != null) {
						getValue().setFormFields(null);
					}
					selectedFieldsContainer.removeAllItems();
					firstPageLoad = false; // Prevent reloading on formType change
				}
				updateDataProvider();
			}
		});

		setRequired(true, FormBuilderDto.DISEASE, FormBuilderDto.FORM_TYPE);
	}

	private void moveFieldsToSelected(Set<String> selectedIds) {
		for (String id : selectedIds) {
			Item item = availableFieldsContainer.getItem(id);
			if (item != null) {
				FormFieldIndexDto field = (FormFieldIndexDto) item.getItemProperty(PROPERTY_FIELD).getValue();
				if (field != null) {
					// Check for duplicates by UUID
					if (selectedFieldsContainer.getItem(field.getUuid()) == null) {
						Item selectedItem = selectedFieldsContainer.addItem(field.getUuid());
						if (selectedItem != null) {
							int order = selectedFieldsContainer.size();
							selectedItem.getItemProperty(PROPERTY_FIELD).setValue(field);
							selectedItem.getItemProperty(PROPERTY_ORDER).setValue(order);
							// Show description instead of fieldName
							String description = field.getDescription() != null && !field.getDescription().isEmpty() 
								? field.getDescription() 
								: field.getFieldName();
							selectedItem.getItemProperty(PROPERTY_NAME).setValue(description);
						}
						availableFieldsContainer.removeItem(id);
					}
				}
			}
		}
		availableFieldsTable.setValue(null);
		reorderItems();
		updateFormFieldsList();
		updateCounters();
		// Re-apply search filter after removing items
		filterAvailableFields(availableFieldsSearch.getValue());
	}

	private void moveFieldsToAvailable(Set<String> selectedIds) {
		for (String id : selectedIds) {
			Item item = selectedFieldsContainer.getItem(id);
			if (item != null) {
				FormFieldIndexDto field = (FormFieldIndexDto) item.getItemProperty(PROPERTY_FIELD).getValue();
				if (field != null) {
					selectedFieldsContainer.removeItem(id);
					// Add back to availableFieldsFullContainer if it doesn't exist
					if (availableFieldsFullContainer != null && availableFieldsFullContainer.getItem(field.getUuid()) == null) {
						Item containerItem = availableFieldsFullContainer.addItem(field.getUuid());
						if (containerItem != null) {
							containerItem.getItemProperty(PROPERTY_FIELD).setValue(field);
							// Show description instead of fieldName
							String description = field.getDescription() != null && !field.getDescription().isEmpty() 
								? field.getDescription() 
								: field.getFieldName();
							containerItem.getItemProperty(PROPERTY_NAME).setValue(description);
						}
					}
					// Add to available fields table
					Item availableItem = availableFieldsContainer.addItem(field.getUuid());
					if (availableItem != null) {
						availableItem.getItemProperty(PROPERTY_FIELD).setValue(field);
						// Show description instead of fieldName
						String description = field.getDescription() != null && !field.getDescription().isEmpty() 
							? field.getDescription() 
							: field.getFieldName();
						availableItem.getItemProperty(PROPERTY_NAME).setValue(description);
					}
				}
			}
		}
		reorderItems();
		updateFormFieldsList();
		updateCounters();
		// Re-apply search filter after adding items back
		filterAvailableFields(availableFieldsSearch.getValue());
	}

	private void moveItem(String itemId, boolean up) {
		int currentIndex = getCurrentItemIndex(itemId);
		if (up && currentIndex > 0) {
			swapItems(currentIndex, currentIndex - 1);
		} else if (!up && currentIndex < selectedFieldsContainer.size() - 1) {
			swapItems(currentIndex, currentIndex + 1);
		}
		reorderItems();
	}

	private void moveItemToTop(String itemId) {
		int currentIndex = getCurrentItemIndex(itemId);
		if (currentIndex > 0) {
			List<Object> itemIds = new ArrayList<>(selectedFieldsContainer.getItemIds());
			Object movedItem = itemIds.remove(currentIndex);
			itemIds.add(0, movedItem);

			IndexedContainer newContainer = new IndexedContainer();
			newContainer.addContainerProperty(PROPERTY_FIELD, FormFieldIndexDto.class, null);
			newContainer.addContainerProperty(PROPERTY_ORDER, Integer.class, null);
			newContainer.addContainerProperty(PROPERTY_NAME, String.class, null);

			for (Object id : itemIds) {
				Item oldItem = selectedFieldsContainer.getItem(id);
				Item newItem = newContainer.addItem(id);
				if (newItem != null && oldItem != null) {
					newItem.getItemProperty(PROPERTY_FIELD).setValue(oldItem.getItemProperty(PROPERTY_FIELD).getValue());
					newItem.getItemProperty(PROPERTY_ORDER).setValue(oldItem.getItemProperty(PROPERTY_ORDER).getValue());
					newItem.getItemProperty(PROPERTY_NAME).setValue(oldItem.getItemProperty(PROPERTY_NAME).getValue());
				}
			}

			selectedFieldsContainer = newContainer;
			selectedFieldsTable.setContainerDataSource(selectedFieldsContainer);
			selectedFieldsTable.setColumnHeader(PROPERTY_ORDER, "Order");
			selectedFieldsTable.setColumnHeader(PROPERTY_NAME, "Description");
			selectedFieldsTable.setVisibleColumns(PROPERTY_ORDER, PROPERTY_NAME);
			selectedFieldsTable.setColumnAlignment(PROPERTY_ORDER, Table.Align.CENTER);
			reorderItems();
			// Don't call filterSelectedFields here - let the button handler do it after all moves are complete
		}
	}

	private void swapItems(int index1, int index2) {
		List<Object> itemIds = new ArrayList<>(selectedFieldsContainer.getItemIds());
		Collections.swap(itemIds, index1, index2);

		IndexedContainer newContainer = new IndexedContainer();
		newContainer.addContainerProperty(PROPERTY_FIELD, FormFieldIndexDto.class, null);
		newContainer.addContainerProperty(PROPERTY_ORDER, Integer.class, null);
		newContainer.addContainerProperty(PROPERTY_NAME, String.class, null);

		for (Object id : itemIds) {
			Item oldItem = selectedFieldsContainer.getItem(id);
			Item newItem = newContainer.addItem(id);
			if (newItem != null && oldItem != null) {
				newItem.getItemProperty(PROPERTY_FIELD).setValue(oldItem.getItemProperty(PROPERTY_FIELD).getValue());
				newItem.getItemProperty(PROPERTY_ORDER).setValue(oldItem.getItemProperty(PROPERTY_ORDER).getValue());
				newItem.getItemProperty(PROPERTY_NAME).setValue(oldItem.getItemProperty(PROPERTY_NAME).getValue());
			}
		}

		selectedFieldsContainer = newContainer;
		selectedFieldsTable.setContainerDataSource(selectedFieldsContainer);
		selectedFieldsTable.setColumnHeader(PROPERTY_ORDER, "Order");
		selectedFieldsTable.setColumnHeader(PROPERTY_NAME, "Description");
		selectedFieldsTable.setVisibleColumns(PROPERTY_ORDER, PROPERTY_NAME);
		selectedFieldsTable.setColumnAlignment(PROPERTY_ORDER, Table.Align.CENTER);
		updateCounters();
		// Don't call filterSelectedFields here - let the button handler do it after all moves are complete
	}

	private int getCurrentItemIndex(String itemId) {
		int index = 0;
		for (Object id : selectedFieldsContainer.getItemIds()) {
			if (id.equals(itemId)) {
				return index;
			}
			index++;
		}
		return -1;
	}

	private void reorderItems() {
		currentOrder = 0;
		getValue().setFormFields(new ArrayList<>());
		for (Object itemId : selectedFieldsContainer.getItemIds()) {
			Item item = selectedFieldsContainer.getItem(itemId);
			if (item != null) {
				item.getItemProperty(PROPERTY_ORDER).setValue(++currentOrder);
				FormFieldIndexDto field = (FormFieldIndexDto) item.getItemProperty(PROPERTY_FIELD).getValue();
				if (field != null) {
					FormFieldReferenceDto dto = new FormFieldReferenceDto();
					dto.setUuid(field.getUuid());
					dto.setCaption(field.getDescription());
					dto.setFieldName(field.getFieldName());
					dto.setDisplayOrder(currentOrder);
					getValue().getFormFields().add(dto);
				}
			}
		}
		updateCounters();
		// Re-apply selected fields filter after reordering
		if (selectedFieldsSearch != null) {
			filterSelectedFields(selectedFieldsSearch.getValue());
		}
	}

	private void updateFormFieldsList() {
		List<FormFieldReferenceDto> selectedDtos = new ArrayList<>();

		for (Object itemId : selectedFieldsContainer.getItemIds()) {
			Item item = selectedFieldsContainer.getItem(itemId);
			if (item != null) {
				FormFieldIndexDto field = (FormFieldIndexDto) item.getItemProperty(PROPERTY_FIELD).getValue();
				if (field != null) {
					FormFieldReferenceDto dto = new FormFieldReferenceDto();
					dto.setUuid(field.getUuid());
					dto.setCaption(field.getDescription());
					dto.setFieldName(field.getFieldName());
					dto.setDisplayOrder(getCurrentItemIndex(itemId.toString()) + 1);
					selectedDtos.add(dto);
				}
			}
		}

		getValue().setFormFields(selectedDtos);
		updateCounters();
	}

	public void updateDataProvider() {
		if (criteria.getFormType() == null) {
			return;
		}

		List<FormFieldIndexDto> formFieldIndexDtos = FacadeProvider.getFormFieldFacade()
			.getIndexList(criteria, null, null, List.of(new SortProperty("fieldName", true)));

		availableFieldsContainer.removeAllItems();

		Set<String> selectedFieldUuids = getValue().getFormFields() != null
			? getValue().getFormFields().stream()
				.map(FormFieldReferenceDto::getUuid)
				.collect(Collectors.toSet())
			: Collections.emptySet();

		// Load existing selected fields BEFORE populating available fields
		// This ensures we have the field data to populate the selected table
		if (firstPageLoad && getValue() != null && getValue().getFormFields() != null && !getValue().getFormFields().isEmpty()) {
			selectedFieldsContainer.removeAllItems();
			// Sort formFields by displayOrder to maintain order
			List<FormFieldReferenceDto> sortedFields = new ArrayList<>(getValue().getFormFields());
			sortedFields.sort((a, b) -> {
				int orderA = a.getDisplayOrder() != null ? a.getDisplayOrder() : 0;
				int orderB = b.getDisplayOrder() != null ? b.getDisplayOrder() : 0;
				return Integer.compare(orderA, orderB);
			});
			
			for (FormFieldReferenceDto fieldRef : sortedFields) {
				// Find the field in the full list (before filtering)
				FormFieldIndexDto field = null;
				for (FormFieldIndexDto dto : formFieldIndexDtos) {
					if (dto.getUuid().equals(fieldRef.getUuid())) {
						field = dto;
						break;
					}
				}
				// If not found in the list, fetch from backend
				if (field == null) {
					de.symeda.sormas.api.infrastructure.fields.FormFieldsDto dto = FacadeProvider.getFormFieldFacade().getByUuid(fieldRef.getUuid());
					if (dto != null) {
						field = new FormFieldIndexDto(dto.getUuid(), dto.getFormType(), dto.getFieldName(), dto.getDescription(), dto.getActive());
					}
				}
				if (field != null) {
					Item item = selectedFieldsContainer.addItem(field.getUuid());
					if (item != null) {
						item.getItemProperty(PROPERTY_FIELD).setValue(field);
						// Backend uses 0-based index, convert to 1-based for display
						int displayOrder = fieldRef.getDisplayOrder() != null ? fieldRef.getDisplayOrder() + 1 : 1;
						item.getItemProperty(PROPERTY_ORDER).setValue(displayOrder);
						// Show description instead of fieldName
						String description = field.getDescription() != null && !field.getDescription().isEmpty() 
							? field.getDescription() 
							: field.getFieldName();
						item.getItemProperty(PROPERTY_NAME).setValue(description);
					}
				}
			}
			// Update the DTO to match what's loaded in the container
			updateFormFieldsList();
			firstPageLoad = false;
		}

		// Now populate available fields (excluding already selected ones)
		availableFieldsFullContainer = new IndexedContainer();
		availableFieldsFullContainer.addContainerProperty(PROPERTY_FIELD, FormFieldIndexDto.class, null);
		availableFieldsFullContainer.addContainerProperty(PROPERTY_NAME, String.class, null);
		
		for (FormFieldIndexDto dto : formFieldIndexDtos) {
			if (!selectedFieldUuids.contains(dto.getUuid())) {
				Item containerItem = availableFieldsFullContainer.addItem(dto.getUuid());
				if (containerItem != null) {
					containerItem.getItemProperty(PROPERTY_FIELD).setValue(dto);
					// Show description instead of fieldName
					String description = dto.getDescription() != null && !dto.getDescription().isEmpty() 
						? dto.getDescription() 
						: dto.getFieldName();
					containerItem.getItemProperty(PROPERTY_NAME).setValue(description);
				}
				// Add to available fields table
				Item availableItem = availableFieldsContainer.addItem(dto.getUuid());
				if (availableItem != null) {
					availableItem.getItemProperty(PROPERTY_FIELD).setValue(dto);
					// Show description instead of fieldName
					String description = dto.getDescription() != null && !dto.getDescription().isEmpty() 
						? dto.getDescription() 
						: dto.getFieldName();
					availableItem.getItemProperty(PROPERTY_NAME).setValue(description);
				}
			}
		}
		
		updateCounters();
		// Apply any existing search filters
		if (availableFieldsSearch != null) {
			filterAvailableFields(availableFieldsSearch.getValue());
		}
		if (selectedFieldsSearch != null) {
			filterSelectedFields(selectedFieldsSearch.getValue());
		}
	}

	@Override
	protected String createHtmlLayout() {
		return HTML_LAYOUT;
	}
	
	private void filterAvailableFields(String searchText) {
		if (availableFieldsFullContainer == null) {
			return;
		}
		
		String filterText = searchText != null ? searchText.toLowerCase().trim() : "";
		
		// Create filtered container
		IndexedContainer filteredContainer = new IndexedContainer();
		filteredContainer.addContainerProperty(PROPERTY_FIELD, FormFieldIndexDto.class, null);
		filteredContainer.addContainerProperty(PROPERTY_NAME, String.class, null);
		
		for (Object itemId : availableFieldsFullContainer.getItemIds()) {
			Item item = availableFieldsFullContainer.getItem(itemId);
			if (item != null) {
				FormFieldIndexDto field = (FormFieldIndexDto) item.getItemProperty(PROPERTY_FIELD).getValue();
				String description = (String) item.getItemProperty(PROPERTY_NAME).getValue();
				
				if (field != null || description != null) {
					String fieldName = field != null && field.getFieldName() != null ? field.getFieldName().toLowerCase() : "";
					String descriptionLower = description != null ? description.toLowerCase() : "";
					
					if (filterText.isEmpty() || fieldName.contains(filterText) || descriptionLower.contains(filterText)) {
						Item newItem = filteredContainer.addItem(itemId);
						if (newItem != null) {
							newItem.getItemProperty(PROPERTY_FIELD).setValue(field);
							newItem.getItemProperty(PROPERTY_NAME).setValue(description);
						}
					}
				}
			}
		}
		
		availableFieldsContainer = filteredContainer;
		availableFieldsTable.setContainerDataSource(availableFieldsContainer);
		availableFieldsTable.setColumnHeader(PROPERTY_NAME, "Description");
		availableFieldsTable.setVisibleColumns(PROPERTY_NAME);
		
		updateCounters();
	}
	
	private void filterSelectedFields(String searchText) {
		if (selectedFieldsContainer == null) {
			return;
		}
		
		String filterText = searchText != null ? searchText.toLowerCase().trim() : "";
		
		// Create filtered container
		IndexedContainer filteredContainer = new IndexedContainer();
		filteredContainer.addContainerProperty(PROPERTY_FIELD, FormFieldIndexDto.class, null);
		filteredContainer.addContainerProperty(PROPERTY_ORDER, Integer.class, null);
		filteredContainer.addContainerProperty(PROPERTY_NAME, String.class, null);
		
		for (Object itemId : selectedFieldsContainer.getItemIds()) {
			Item item = selectedFieldsContainer.getItem(itemId);
			if (item != null) {
				String description = (String) item.getItemProperty(PROPERTY_NAME).getValue();
				FormFieldIndexDto field = (FormFieldIndexDto) item.getItemProperty(PROPERTY_FIELD).getValue();
				
				if (description != null || field != null) {
					String descriptionLower = description != null ? description.toLowerCase() : "";
					String fieldName = field != null && field.getFieldName() != null ? field.getFieldName().toLowerCase() : "";
					
					if (filterText.isEmpty() || descriptionLower.contains(filterText) || fieldName.contains(filterText)) {
						Item newItem = filteredContainer.addItem(itemId);
						if (newItem != null) {
							newItem.getItemProperty(PROPERTY_FIELD).setValue(field);
							newItem.getItemProperty(PROPERTY_ORDER).setValue(item.getItemProperty(PROPERTY_ORDER).getValue());
							newItem.getItemProperty(PROPERTY_NAME).setValue(description);
						}
					}
				}
			}
		}
		
		selectedFieldsTable.setContainerDataSource(filteredContainer);
		selectedFieldsTable.setColumnHeader(PROPERTY_ORDER, "Order");
		selectedFieldsTable.setColumnHeader(PROPERTY_NAME, "Description");
		selectedFieldsTable.setVisibleColumns(PROPERTY_ORDER, PROPERTY_NAME);
		selectedFieldsTable.setColumnAlignment(PROPERTY_ORDER, Table.Align.CENTER);
		
		updateCounters();
	}
	
	private void updateCounters() {
		// Update available fields count
		int availableCount = availableFieldsContainer != null && availableFieldsContainer.getItemIds() != null 
			? availableFieldsContainer.getItemIds().size() : 0;
		if (availableFieldsFullContainer != null) {
			int totalAvailable = availableFieldsFullContainer.getItemIds() != null ? availableFieldsFullContainer.getItemIds().size() : 0;
			availableFieldsCountLabel.setValue("Available: " + availableCount + (availableCount != totalAvailable ? " (of " + totalAvailable + ")" : ""));
		} else {
			availableFieldsCountLabel.setValue("Available: " + availableCount);
		}
		
		// Update selected fields count
		int selectedCount = selectedFieldsContainer != null && selectedFieldsContainer.getItemIds() != null 
			? selectedFieldsContainer.getItemIds().size() : 0;
		selectedFieldsCountLabel.setValue("Selected: " + selectedCount);
	}

	@Override
	public void setValue(FormBuilderDto newFieldValue) {
		super.setValue(newFieldValue);
		if (newFieldValue != null) {
			// Reset firstPageLoad flag when loading an existing entity with formFields
			if (newFieldValue.getUuid() != null && newFieldValue.getFormFields() != null && !newFieldValue.getFormFields().isEmpty()) {
				firstPageLoad = true;
			} else if (newFieldValue.getUuid() == null) {
				// New entity - reset firstPageLoad
				firstPageLoad = true;
			}
			if (newFieldValue.getFormType() != null) {
				criteria.formType(newFieldValue.getFormType());
				// Ensure relevanceStatus is set to ACTIVE
				if (criteria.getRelevanceStatus() == null) {
					criteria.relevanceStatus(EntityRelevanceStatus.ACTIVE);
				}
				// Delay updateDataProvider to ensure formFields are loaded
				updateDataProvider();
			}
		}
	}
}

