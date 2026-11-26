# Controls

Controls are components that can hold a value. They are the main way users interact with the application, and they are usually placed in forms so their values can be submitted.

## Control (abstract)

All controls share the following properties. `null` means that no value is assigned; context-dependent defaults are described in the table.

| Property | Type | Default | Description |
| --- | --- | --- | --- |
| `value` | `Object` | `null` | The value held by the control. |
| `valueType` | `String` | `null` on `Control`; concrete controls set their own type | Type name used to serialize and deserialize the value. |
| `defaultValue` | `Object` | `null` | Value used when no value has been assigned. |
| `nullable` | `Boolean` | `true` | Whether the control can hold a `null` value. |
| `invalidChars` | `String` | `null` | Characters rejected as input. Takes precedence over `validChars` and `pattern`. |
| `validChars` | `String` | `null` | Characters accepted as input. Takes precedence over `pattern`. |
| `pattern` | `String` | `''` | Regular expression used to validate input when `invalidChars` and `validChars` are unset. |
| `prettyPrinter` | `String` | `null` | Name of the pretty printer used to render the value. |
| `transformer` | `String` | `null` | Name of the transformer used to render the value. |
| `textPrefix` | `String` | Current controller name | Prefix used to look up localized text in `messages.properties`. |
| `renderTextPrefix` | `Boolean` | `true` | Whether to display the `textPrefix`; some controls override this. |
| `textArgs` | `List` | `null` | Arguments passed to localized text. |
| `textStyle` | `TextStyle` | `BOLD` | Text style applied by controls that render text. |

## TextField

A text field.

```groovy
c.form.addField(
    class: TextField,
    id: 'username',
    icon: 'fa-user',
    textStyle: TextStyle.LINE_THROUGH,
)
```

### Properties

| Name | Type | Default | Description |
| --- | --- | --- | --- |
| `inputType` | `TextFieldInputType` | `TEXT` | HTML input type. |
| `inputMode` | `TextFieldInputMode` | `TEXT` | HTML input mode. |
| `icon` | `String` | `''` | An icon to display inside the control. Choose one from [Font Awesome](https://fontawesome.com/). |
| `prefix` | `String` | `''` | Text to display before the editable area of the control. |
| `maxSize` | `Integer` | `0` | Maximum number of characters the user can enter; `0` means no limit. |
| `displayMaxSize` | `Boolean` | `false` | Whether to display the remaining-character counter. A positive `maxSize` is needed for a limit. |
| `placeholder` | `String` | `''` | Text to display when the control is empty. |
| `autocomplete` | `Boolean` | `false` | Whether browser autocomplete is enabled for this field. |
| `autoSelect` | `Boolean` | `true` | Whether to select the field value when it receives focus. |
| `textTransform` | `TextTransform` | `NONE` | Transforms the input while typing. |
| `textStyle` | `TextStyle` | `BOLD` | Determines the text style. |
| `pattern` | `String` | `''` | A regular expression used to accept only specific input. For example, `'^[0-9\\.\\,]*$'` accepts only numbers, dots, and commas. |
| `renderTextPrefix` | `Boolean` | `false` | Whether the field's localized text prefix is rendered; this overrides the `Control` default. |
| `onChangeAsync` | `Boolean` | `false` | Whether the `change` event is triggered asynchronously while typing. |

`textTransform` values:

- `UPPERCASE`
- `LOWERCASE`
- `CAPITALIZE` each word

`textStyle` values:

- `NORMAL`
- `BOLD`
- `ITALIC`
- `MONOSPACE`
- `UNDERLINE`
- `LINE_THROUGH`

### Methods

| Method | Description |
| --- | --- |
| `addAction()` | Adds an action button at the end of the control. See [link](components.md#link). |

### Events

| Event | Description |
| --- | --- |
| `load` | Triggered once the content is loaded. |
| `change` | Triggered when the value changes. |

## Select

Displays a list of options to choose from.

### Properties

| Name | Type | Default | Description |
| --- | --- | --- | --- |
| `optionsFromRecordset` | `List<Map>` or `List<Object>` or `GORM Recordset` | `null` | Sets options from a recordset. |
| `optionsFromList` | `List` | `null` | Sets options from list items. The key of each item matches the item value. |
| `optionsFromEnum` | `Enum` | `null` | Sets options from an enum. The key of each item matches the item value. |
| `options` | `Map` | `[]` (resolved options) | Sets options from map entries using key/value pairs. |
| `keys` | `List<String>` | `[]` | Column names used as option keys. For record options, Goowee uses `id` automatically when present. |
| `keysSeparator` | `String` | `,` | Separator used when multiple keys are combined into an option ID. |
| `exclude` | `List` | `[]` | Values excluded when options are built from a list or enum. |
| `forEachOption` | `Closure` | `null` | Closure executed for each option while options are built. |
| `prettyPrinter` | `Class` or `String` | `null` (inferred from option type when possible) | Use the specified pretty printer to display options. See `registerPrettyPrinter()`. |
| `transformer` | `String` | `null` | Name of the transformer used to display options. See `registerTransformer()`. |
| `textStyle` | `TextStyle` | `BOLD` | Determines the text style. |
| `textPrefix` | `String` | Current controller name | Prefix added to each item so it can be localized through `message.properties`. |
| `renderTextPrefix` | `Boolean` | `true` | Whether to display `textPrefix`. |
| `placeholder` | `String` | `control.select.placeholder` | Text to display when no option is selected. |
| `allowClear` | `Boolean` | `true` for recordset options or multiple selection; otherwise `false` | If `true`, the selection can be cleared. |
| `autoSelect` | `Boolean` | `true` | When there is one option and the field is non-nullable, it is automatically selected. |
| `multiple` | `Boolean` | `false` | Enables multiple selection. |
| `search` | `Boolean` | `true` for recordset and map options; `false` for list and enum options | Displays a search box to filter the available options. Without a `search` event, filtering is client-side. With a `search` event, Goowee requests options from the server when the list opens and when the search text changes. |
| `searchMinInputLength` | `Integer` | `0` | Currently has no effect in the client. A configured `search` event runs when the list opens and when the search text changes, regardless of this value. |

`textStyle` values:

- `NORMAL`
- `BOLD`
- `ITALIC`
- `MONOSPACE`
- `UNDERLINE`
- `LINE_THROUGH`

### Methods

| Method | Description |
| --- | --- |
| `Select.optionsFromRecordset(recordset: ...)` | Returns a `Map` of options to use in a transition. See [search on server](#search-on-server). Accepts a `Map` with the following arguments: `keys`, `keysSeparator`, `prettyPrinter`, `transformer`, `textPrefix`, `renderTextPrefix`, `locale`. |
| `Select.optionsFromList(list: ...)` | Returns a `Map` of options to use in a transition. See [search on server](#search-on-server). Accepts a `Map` with the arguments listed above. |
| `Select.optionsFromEnum(enum: ...)` | Returns a `Map` of options to use in a transition. See [search on server](#search-on-server). Accepts a `Map` with the arguments listed above. |
| `Select.options(options: ...)` | Returns a `Map` of options to use in a transition. See [search on server](#search-on-server). Accepts a `Map` with the arguments listed above. |

### Events

| Event | Description |
| --- | --- |
| `load` | Triggered once the content is loaded. |
| `change` | Triggered when the value changes. |
| `search` | Triggered when the list opens and when the search text changes. |

### Search on Server

When a `search` event is configured, the control requests its initial options when the list opens and requests matching options as the user types. Each response must be a `Transition` that sets the `options` property on this `Select`.

```groovy
c.form.with {
    addField(
        class: Select,
        id: 'activity',
        onLoad: 'onLoadActivity', // <1>
        onChange: 'onChangeActivity',
        onSearch: 'onSearchActivity', // <2>
        submit: ['form'],
        allowClear: true,
    )
}
```

- `<1>` When a value is selected, the `load` event returns that option so the control can display its label. It returns no options when there is no selected value.
- `<2>` The `search` action returns matching options in a `Transition`.

Create the following actions.

```groovy
ActivityService activityService

def onLoadActivity() {
    def t = createTransition()
    def activities = activityService.list(id: params.activity) // <1>
    def options = Select.optionsFromRecordset(recordset: activities)
    t.set('activity', 'options', options)
    display transition: t
}

def onSearchActivity() {
    def t = createTransition()
    def activities = activityService.list(find: params.activity) // <2>
    def options = Select.optionsFromRecordset(recordset: activities)
    t.set('activity', 'options', options)
    display transition: t
}

def onChangeActivity() {
    def t = createTransition()
    // Do something...
    display transition: t
}
```

- `<1>` `params.activity` contains the selected ID.
- `<2>` `params.activity` contains the search string.

## Checkbox

A checkbox is a way to interact with `Boolean` values.

```groovy
c.form.with {
    addField(
        class: Checkbox,
        id: 'fullscreen',
        displayLabel: false,
        cols: 3,
    )
}
```

### Properties

| Name | Type | Default | Description |
| --- | --- | --- | --- |
| `text` | `String` | Current `controller.id.text` message key | The text to display. |
| `textArgs` | `List` | `[]` | Arguments passed to localized text. |
| `simple` | `Boolean` | `false` | Whether to render the checkbox with the simple style. |
| `option` | `Map` | `null` | A single key/value option represented by this checkbox. |
| `optionKey` | `Object` | `''` | Submitted value when this checkbox is checked. |
| `optionValue` | `Object` | `''` | Displayed value for `optionKey`. |

### Events

| Event | Description |
| --- | --- |
| `change` | Triggered when the value changes. |

## MultipleCheckbox

Manages multiple checkboxes as if they were a `Select` control with many options. See [Select](#select).

| Property | Type | Default | Description |
| --- | --- | --- | --- |
| `optionsFromRecordset`, `optionsFromList`, `optionsFromEnum` | `Collection`, `List`, or `Enum` | `null` | Optional source used to build the checkboxes. |
| `options` | `Map` | `[]` (resolved options) | Map of option values and labels. |
| `keys` | `List<String>` | `[]` | Key property names. For record options, Goowee uses `id` automatically when present. |
| `keysSeparator` | `String` | `,` | Separator used when multiple keys are combined. |
| `exclude` | `List` | `[]` | Values excluded from list or enum options. |
| `forEachOption` | `Closure` | `null` | Closure executed for each option. |
| `textPrefix` | `String` | Current controller name | Prefix used to localize option labels. |
| `simple` | `Boolean` | `false` | Whether to render each checkbox with the simple style. |

## Textarea

A text area that can span multiple lines in a form. It inherits the other [TextField](#textfield) properties.

```groovy
c.form.with {
    addField(
        class: Textarea,
        id: 'textarea',
        maxSize: 100,
        cols: 12,
        rows: 5,
    )
}
```

### Properties

| Name | Type | Default | Description |
| --- | --- | --- | --- |
| `maxSize` | `Integer` | `0` | Maximum number of characters the user can enter. |
| `autoSelect` | `Boolean` | `false` | Whether to select the field value when it receives focus; overrides the `TextField` default. |
| `displayMaxSize` | `Boolean` | `true` | Whether to display the remaining-character counter. A positive `maxSize` is needed to show a limit. |
| `acceptNewLine` | `Boolean` | `true` | Whether the user can enter line breaks. |
| `encode` | `Boolean` | `false` | Whether the submitted text is Base64 encoded. |

### Events

| Event | Description |
| --- | --- |
| `change` | Triggered when the value changes. |

## QuantityField

A text field to input quantities.

```groovy
c.form.with {
    addField(
        class: QuantityField,
        id: 'quantity',
        defaultUnit: QuantityUnit.KM,
        availableUnits: quantityService.listAllUnits(),
    )
}
```

### Properties

| Name | Type | Default | Description |
| --- | --- | --- | --- |
| `decimals` | `Integer` | `2` | Allowed decimal digits. |
| `negative` | `Boolean` | `false` | Whether negative values are allowed. |
| `availableUnits` | `List<QuantityUnit>` | `[]` | Units available for selection. |
| `unitOptions` | `Map<String, String>` | `[:]` | Available units rendered as key/value options. |
| `defaultUnit` | `QuantityUnit` | First available unit; otherwise `QuantityUnit.ND` | The default unit to display. |
| `inputMode` | `TextFieldInputMode` | `DECIMAL` | Follows the default `decimals: 2` setting. |
| `pattern` | `String` | `^[0-9]*((?<=[0-9])[.,])?[0-9]{0,2}$` | Generated from `decimals` and `negative`. |

### Events

| Event | Description |
| --- | --- |
| `load` | Triggered once the content is loaded. |
| `change` | Triggered when the value changes. |

## MoneyField

A text field to input currency values.

```groovy
c.form.with {
    addField(
        class: MoneyField,
        id: 'salary',
        decimals: 0,
    )
}
```

### Properties

| Name | Type | Default | Description |
| --- | --- | --- | --- |
| `decimals` | `Integer` | `2` | Allowed decimal digits. |
| `negative` | `Boolean` | `false` | Whether negative values are allowed. |
| `currency` | `String` | `EUR` | Currency prefix used when the value is not already a `Money` instance. |
| `inputMode` | `TextFieldInputMode` | `DECIMAL` | Follows the default `decimals: 2` setting. |
| `pattern` | `String` | `^[0-9]*((?<=[0-9])[.,])?[0-9]{0,2}$` | Generated from `decimals` and `negative`. |

### Events

| Event | Description |
| --- | --- |
| `load` | Triggered once the content is loaded. |
| `change` | Triggered when the value changes. |

## NumberField

A text field used to enter numeric values.

```groovy
c.form.with {
    addField(
        class: NumberField,
        id: 'number',
        min: -2,
        max: 10,
    )
}
```

### Properties

| Name | Type | Default | Description |
| --- | --- | --- | --- |
| `decimals` | `Integer` | `0` | Allowed decimal digits. |
| `negative` | `Boolean` | `true` | Whether negative values are allowed. |
| `pattern` | `String` | `^-?[0-9]*$` | Generated from `decimals` and `negative`. |
| `inputMode` | `TextFieldInputMode` | `NUMERIC`; `DECIMAL` when `decimals` is greater than `0` | Set automatically when `decimals` changes. |
| `min` | `Integer` | `null` | Minimum number the user can enter. |
| `max` | `Integer` | `null` | Maximum number the user can enter. |

## Date and time controls

`DateField`, `MonthField`, `TimeField`, and `DateTimeField` accept optional boundaries, which default to `null`. They default to `autoPopulate: false`; `TimeField` and `DateTimeField` also default to `renderSeconds: false`.

| Control | Property | Type | Default |
| --- | --- | --- | --- |
| `DateTimeField` | `min`, `max` | `LocalDate`, `LocalTime`, or `LocalDateTime` | `null` |
| `DateTimeField` | `timeStep` | `Integer` | `null` (picker default) |
| `DateTimeField` | `pattern` | `String` | `^[0-9/: ]*$` |
| `DateTimeField` | `autoPopulate`, `renderSeconds` | `Boolean` | `false` |
| `DateField` | `min`, `max` | `LocalDate` | `null` |
| `DateField` | `pattern` | `String` | `^[0-9/]*$` |
| `DateField` | `autoPopulate` | `Boolean` | `false` |
| `MonthField` | `min`, `max` (inherited) | `LocalDate`, `LocalTime`, or `LocalDateTime` | `null` |
| `MonthField` | `pattern` | `String` | `^[0-9/]*$` |
| `MonthField` | `autoPopulate` (inherited) | `Boolean` | `false` |
| `TimeField` | `min`, `max` | `LocalTime` | `null` |
| `TimeField` | `timeStep` | `Integer` | `null` (picker default) |
| `TimeField` | `pattern` | `String` | `^[0-9:]*$` |
| `TimeField` | `autoPopulate`, `renderSeconds` | `Boolean` | `false` |

## Specialized text controls

These controls inherit the `TextField` properties and apply their own initial settings.

| Control | Property | Default |
| --- | --- | --- |
| `EmailField` | `inputMode`, `icon` | `EMAIL`, `fa-at` |
| `EmailField` | `pattern` | `^(?!.*@.*@)(?!.*(\.)\1).[A-Za-z0-9_\-\.@]*$` |
| `TelephoneField` | `inputMode`, `icon`, `pattern` | `TEL`, `fa-phone`, `^[0-9\+]*$` |
| `UrlField` | `inputMode`, `icon`, `pattern` | `URL`, `fa-globe`, `^[a-zA-Z0-9@\-_.:\/~#=!?&\*()]*$` |
| `PasswordField` | `inputType`, `icon`, `autocomplete` | `PASSWORD`, `fa-lock`, `false` |

## Upload

| Property | Type | Default | Description |
| --- | --- | --- | --- |
| `labelIdle` | `String` | `control.upload.message` | FilePond label shown in the idle upload area. |
| `allowMultiple` | `Boolean` | `false` | Whether multiple files can be added. |
| `acceptedFileTypes` | `List` | `[]` | Allowed MIME types; an empty list accepts all file types. |
| `maxFiles` | `Integer` | `null` | Maximum number of uploaded files. |
| `maxFileSize` | `String` | `20MB` | Maximum file size. |

## HiddenField

`HiddenField` defaults to `display: false` and `skipFocus: true`. Its `valueType` is inferred from `value`, or defaults to `STRING` when there is no value.

## ProgressBar

| Property | Type | Default | Description |
| --- | --- | --- | --- |
| `min` | `BigDecimal` | `0` | Lower bound. |
| `max` | `BigDecimal` | `100` | Upper bound. |
| `value` | `BigDecimal` | `0` | Current value. |
| `decimals` | `Integer` | `0` | Decimal digits shown. |
| `percentage` | `Boolean` | `true` | Whether to display the normalized percentage. |
| `indefinite` | `Boolean` | `true` if `max` is omitted; otherwise `false` | Whether to display the animated indefinite indicator. |
