# Guide: Adding a Field to an Entity in SORMAS

This guide walks through the process of adding a new field to an entity in SORMAS, using the `idsrDiagnosis` field added to the `Case` entity as an example.

## Overview

When adding a field to an entity in SORMAS, you need to update multiple layers:
1. Database schema
2. Backend entity class
3. API DTO class
4. Facade mapping
5. UI forms
6. Internationalization (i18n)
7. Bulk edit support (if applicable)

## Step-by-Step Guide

### 1. Database Schema

Add the column to both the main table and its history table in `sormas-backend/src/main/resources/sql/sormas_schema.sql`.

**Example:**
```sql
-- Add IDSR diagnosis field to cases
alter table cases add idsrdiagnosis varchar(255);
alter table cases_history add idsrdiagnosis varchar(255);

INSERT INTO schema_version (version_number, comment) VALUES (579, 'Add IDSR diagnosis field to cases');
```

**Notes:**
- Use lowercase with no underscores for column names (e.g., `idsrdiagnosis` not `idsr_diagnosis`)
- For enum types, use `varchar(255)`
- Always add to both the main table and `_history` table
- Increment the schema version number
- Place new migrations before the final comment line

### 2. Backend Entity Class

Add the field to the entity class in `sormas-backend/src/main/java/de/symeda/sormas/backend/[entity]/[Entity].java`.

**Example for Case entity:**
```java
// Add constant (if needed for queries)
public static final String IDSR_DIAGNOSIS = "idsrDiagnosis";

// Add field declaration
private IdsrType idsrDiagnosis;

// Add getter and setter with JPA annotations
@Enumerated(EnumType.STRING)
public IdsrType getIdsrDiagnosis() {
    return idsrDiagnosis;
}

public void setIdsrDiagnosis(IdsrType idsrDiagnosis) {
    this.idsrDiagnosis = idsrDiagnosis;
}
```

**Notes:**
- Use `@Enumerated(EnumType.STRING)` for enum types
- Use `@Column` annotation if you need to specify column name or constraints
- Follow existing naming conventions in the class

### 3. API DTO Class

Add the field to the DTO class in `sormas-api/src/main/java/de/symeda/sormas/api/[entity]/[Entity]DataDto.java`.

**Example:**
```java
// Add constant
public static final String IDSR_DIAGNOSIS = "idsrDiagnosis";

// Add field with annotations (if needed)
@Diseases({
    Disease.IMMEDIATE_CASE_BASED_FORM_OTHER_CONDITIONS })
@Outbreaks
private IdsrType idsrDiagnosis;

// Add getter and setter
public IdsrType getIdsrDiagnosis() {
    return idsrDiagnosis;
}

public void setIdsrDiagnosis(IdsrType idsrDiagnosis) {
    this.idsrDiagnosis = idsrDiagnosis;
}
```

**Notes:**
- Add appropriate annotations like `@Diseases`, `@Outbreaks`, `@SensitiveData`, etc., based on field requirements
- Use validation annotations if needed (`@NotNull`, `@Size`, etc.)

### 4. Facade Mapping

Update the facade class in `sormas-backend/src/main/java/de/symeda/sormas/backend/[entity]/[Entity]FacadeEjb.java`.

**A. In `toDto()` or `to[Entity]Dto()` method:**
```java
target.setIdsrDiagnosis(source.getIdsrDiagnosis());
```

**B. In `fillOrBuildEntity()` method:**
```java
target.setIdsrDiagnosis(source.getIdsrDiagnosis());
```

**C. In bulk edit method (if applicable):**
```java
existingCase.setIdsrDiagnosis(updatedCaseBulkEditData.getIdsrDiagnosis());
```

**Example locations in CaseFacadeEjb:**
- `toCaseDto()` method (around line 3048)
- `fillOrBuildEntity()` method (around line 3239)
- Bulk edit method (around line 1647)

### 5. UI Forms

Add the field to relevant UI forms.

#### A. CaseDataForm (Edit Form)

**In HTML layout constant:**
```java
fluidRow(
    fluidColumnLoc(6, 0, CaseDataDto.DISEASE),
    fluidColumnLoc(6, 0, CaseDataDto.IDSR_DIAGNOSIS)) +
```

**In `addFields()` method:**
```java
addField(CaseDataDto.IDSR_DIAGNOSIS, NullableOptionGroup.class);
```

**In visibility/accessibility setup:**
```java
if (isVisibleAllowed(CaseDataDto.IDSR_DIAGNOSIS)) {
    FieldHelper.setVisibleWhen(
        getFieldGroup(),
        Arrays.asList(CaseDataDto.IDSR_DIAGNOSIS),
        CaseDataDto.DISEASE,
        Arrays.asList(Disease.IMMEDIATE_CASE_BASED_FORM_OTHER_CONDITIONS),
        true);
}
```

#### B. CaseCreateForm (Create Form)

**In HTML layout constant:**
```java
fluidRow(
    fluidColumnLoc(6, 0, CaseDataDto.DISEASE),
    fluidColumnLoc(6, 0, CaseDataDto.IDSR_DIAGNOSIS)) +
```

**In `addFields()` method:**
```java
addField(CaseDataDto.IDSR_DIAGNOSIS, NullableOptionGroup.class);
```

**In visibility setup:**
```java
FieldHelper.setVisibleWhen(
    getFieldGroup(), 
    Arrays.asList(CaseDataDto.IDSR_DIAGNOSIS), 
    CaseDataDto.DISEASE, 
    Arrays.asList(Disease.IMMEDIATE_CASE_BASED_FORM_OTHER_CONDITIONS), 
    true);
```

#### C. BulkCaseDataForm (Bulk Edit)

**In `addFields()` method:**
```java
addField(CaseBulkEditData.IDSR_DIAGNOSIS, NullableOptionGroup.class);
```

**In visibility setup:**
```java
if (isVisibleAllowed(CaseBulkEditData.IDSR_DIAGNOSIS)) {
    FieldHelper.setVisibleWhen(
        getFieldGroup(),
        Arrays.asList(CaseBulkEditData.IDSR_DIAGNOSIS),
        CaseBulkEditData.DISEASE,
        Arrays.asList(Disease.IMMEDIATE_CASE_BASED_FORM_OTHER_CONDITIONS),
        true);
}
```

**Notes:**
- Use `NullableOptionGroup.class` for enum fields
- Use `TextField.class` for string fields
- Use `DateField.class` for date fields
- Set visibility conditions based on business rules

### 6. Bulk Edit Data (if applicable)

If the field should be editable in bulk operations, add it to `CaseBulkEditData.java`:

**Add constant:**
```java
public static final String IDSR_DIAGNOSIS = "idsrDiagnosis";
```

**Add field:**
```java
private IdsrType idsrDiagnosis;
```

**Add getter and setter:**
```java
public IdsrType getIdsrDiagnosis() {
    return idsrDiagnosis;
}

public void setIdsrDiagnosis(IdsrType idsrDiagnosis) {
    this.idsrDiagnosis = idsrDiagnosis;
}
```

### 7. Internationalization (i18n)

Add translations for enum values in properties files.

**In `sormas-api/src/main/resources/enum.properties`:**
```properties
# IdsrType
IdsrType.ANTHRAX = Anthrax
IdsrType.DENGUE_FEVER = Dengue Fever
IdsrType.PLAGUE = Plague
IdsrType.RABIES = Rabies
IdsrType.SMALLPOX = Smallpox
IdsrType.SARS = SARS
IdsrType.OTHER = Other
```

**In `sormas-api/src/main/resources/enum_en-GM.properties` (or other locale files):**
```properties
# IdsrType
IdsrType.ANTHRAX = Anthrax
IdsrType.DENGUE_FEVER = Dengue Fever
IdsrType.PLAGUE = Plague
IdsrType.RABIES = Rabies
IdsrType.SMALLPOX = Smallpox
IdsrType.SARS = SARS
IdsrType.OTHER = Other
```

**For field captions, add to `sormas-api/src/main/resources/strings.properties`:**
```properties
CaseData.idsrDiagnosis = IDSR Diagnosis
```

## Checklist

When adding a new field, ensure you've:

- [ ] Added column to database schema (main table + history table)
- [ ] Incremented schema version
- [ ] Added field to backend entity class with proper annotations
- [ ] Added field to API DTO class with proper annotations
- [ ] Updated facade `toDto()` method
- [ ] Updated facade `fillOrBuildEntity()` method
- [ ] Updated bulk edit method (if applicable)
- [ ] Added field to CaseDataForm (edit form)
- [ ] Added field to CaseCreateForm (create form)
- [ ] Added field to BulkCaseDataForm (if applicable)
- [ ] Set up visibility conditions
- [ ] Added to CaseBulkEditData (if applicable)
- [ ] Added enum translations to i18n files
- [ ] Added field caption translations (if needed)
- [ ] Tested the changes

## Common Field Types

### Enum Field
- Database: `varchar(255)`
- Entity: `@Enumerated(EnumType.STRING)`
- UI: `NullableOptionGroup.class`
- Example: `IdsrType`, `PlagueType`, `RabiesType`

### String Field
- Database: `varchar(255)` or `text` for longer content
- Entity: `@Column(length = CHARACTER_LIMIT_DEFAULT)`
- UI: `TextField.class`
- Example: `diseaseDetails`, `healthFacilityDetails`

### Date Field
- Database: `timestamp without time zone`
- Entity: `@Temporal(TemporalType.TIMESTAMP)` or `@Temporal(TemporalType.DATE)`
- UI: `DateField.class`
- Example: `reportDate`, `outcomeDate`

### Boolean Field
- Database: `boolean`
- Entity: `@Column`
- UI: `CheckBox.class`
- Example: `nosocomialOutbreak`, `sharedToCountry`

### Number Field
- Database: `integer`, `bigint`, `numeric`, etc.
- Entity: `@Column` or `@Column(columnDefinition = "...")`
- UI: `TextField.class` with validators
- Example: `caseAge`, `caseIdIsm`

## Tips

1. **Follow existing patterns**: Look at similar fields in the same entity for consistency
2. **Column naming**: Database columns use lowercase without underscores (e.g., `idsrdiagnosis`)
3. **Java naming**: Use camelCase for Java fields (e.g., `idsrDiagnosis`)
4. **Constants**: Always define constants for field names used in queries and UI
5. **Visibility**: Use `FieldHelper.setVisibleWhen()` for conditional field visibility
6. **Validation**: Add appropriate validation annotations in the DTO
7. **History tables**: Always update both main and history tables
8. **Schema version**: Always increment and document schema version changes

## Example: Complete Flow for `idsrDiagnosis`

1. ✅ Database: Added `idsrdiagnosis varchar(255)` to `cases` and `cases_history`
2. ✅ Entity: Added field with `@Enumerated(EnumType.STRING)` annotation
3. ✅ DTO: Added field with `@Diseases` and `@Outbreaks` annotations
4. ✅ Facade: Added mappings in `toCaseDto()` and `fillOrBuildEntity()`
5. ✅ UI: Added to CaseDataForm, CaseCreateForm, and BulkCaseDataForm
6. ✅ Visibility: Set to show only for `IMMEDIATE_CASE_BASED_FORM_OTHER_CONDITIONS` disease
7. ✅ Bulk Edit: Added to CaseBulkEditData with mapping in facade
8. ✅ i18n: Added IdsrType enum translations

## Related Files Modified

For the `idsrDiagnosis` example:
- `sormas-backend/src/main/resources/sql/sormas_schema.sql`
- `sormas-backend/src/main/java/de/symeda/sormas/backend/caze/Case.java`
- `sormas-api/src/main/java/de/symeda/sormas/api/caze/CaseDataDto.java`
- `sormas-backend/src/main/java/de/symeda/sormas/backend/caze/CaseFacadeEjb.java`
- `sormas-ui/src/main/java/de/symeda/sormas/ui/caze/CaseDataForm.java`
- `sormas-ui/src/main/java/de/symeda/sormas/ui/caze/CaseCreateForm.java`
- `sormas-ui/src/main/java/de/symeda/sormas/ui/caze/BulkCaseDataForm.java`
- `sormas-api/src/main/java/de/symeda/sormas/api/caze/CaseBulkEditData.java`
- `sormas-api/src/main/resources/enum.properties`
- `sormas-api/src/main/resources/enum_en-GM.properties`

