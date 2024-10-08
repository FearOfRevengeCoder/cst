
### CST Version 6.1

#### Enhancements and Bug Fixes:

1. **Null Checks**:
   - Added null checks to ensure that all potentially null values are handled correctly, preventing potential crashes and errors.

2. **Logging**:
   - Implemented logging to track the plugin's loading and disabling processes.
   - Added logging for error handling during command registration to quickly identify and resolve issues.

3. **Error Handling**:
   - Improved error handling to prevent potential crashes. For example, added checks for correct time input and valid actions for `ClickEvent`.

4. **TextComponent Usage**:
   - Ensured that `TextComponent` is used correctly, especially when working with `ClickEvent` and `HoverEvent`, to avoid potential issues with message formatting.

5. **Plugin Folder Creation**:
   - Ensured that the plugin's folder is created before attempting to create files, preventing errors related to missing directories.

6. **File Loading Exception Handling**:
   - Added exception handling for file loading. If a file fails to load due to a YAML format error, it will be logged, and an empty configuration will be created to prevent `NullPointerException`.

7. **Message Logging**:
   - Added logging for cases where a message is not found in the messages file, helping to identify and resolve missing message keys.

#### Summary:
These changes make the plugin more robust and easier to debug. Now, if something goes wrong, you can quickly identify the cause of the problem thanks to the added logging and error handling.
