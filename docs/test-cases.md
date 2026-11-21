# Test Cases

Manual test case specifications. Every automated test references its ID in the TestNG `description`.

**Groups:** `smoke` – critical path, run on every push · `regression` – full suite.

## 1. Authentication

### AUTH-01 – Login with valid admin credentials

| Field | Value |
| --- | --- |
| Group | smoke |
| Precondition | User is logged out and on the Login page |
| Test data | Admin username and password of the target environment |

| # | Step | Expected result |
| --- | --- | --- |
| 1 | Enter a valid username | Value is displayed in the Username field |
| 2 | Enter a valid password | Value is masked in the Password field |
| 3 | Click **Login** | Dashboard page opens, URL contains `/dashboard`, page header shows "Dashboard" |

### AUTH-02 – Login with invalid credentials

| Field | Value |
| --- | --- |
| Group | regression |
| Precondition | User is on the Login page |
| Test data | Data set from `login-data.json` |

| # | Data set | Expected result |
| --- | --- | --- |
| 1 | Valid username, wrong password | "Invalid credentials" alert |
| 2 | Wrong username, valid password | "Invalid credentials" alert |
| 3 | Empty username | "Required" under Username |
| 4 | Empty password | "Required" under Password |
| 5 | Both fields empty | "Required" under both fields |
| 6 | Username with leading/trailing spaces | "Invalid credentials" alert (username is not trimmed) |
| 7 | Username with special characters | "Invalid credentials" alert |
| 8 | Password in wrong letter case | "Invalid credentials" alert |

In every case the user stays on the Login page.

### AUTH-03 – Logout from the header menu

| Field | Value |
| --- | --- |
| Group | smoke |
| Precondition | User is logged in as Admin |

| # | Step | Expected result |
| --- | --- | --- |
| 1 | Click the user name in the header | User dropdown opens with a **Logout** item |
| 2 | Click **Logout** | Login page opens, URL contains `/auth/login` |

### AUTH-04 – Browser Back after logout

| Field | Value |
| --- | --- |
| Group | regression |
| Precondition | User is logged in as Admin |

| # | Step | Expected result |
| --- | --- | --- |
| 1 | Log out | Login page opens |
| 2 | Click the browser **Back** button | Either the Login page or a cached copy of the previous page is shown |
| 3 | Reload the page | User is redirected to the Login page; internal pages are not accessible |

> **Observation:** depending on the browser's back/forward cache, step 2 sometimes shows the cached
> Dashboard. The session is already invalid at that point (API calls return `401`), so the test verifies
> access with a reload instead of the URL right after Back.

### AUTH-05 – Direct access to an internal page without a session

| Field | Value |
| --- | --- |
| Group | regression |
| Precondition | User is not logged in |

| # | Step | Expected result |
| --- | --- | --- |
| 1 | Open the PIM employee list URL directly | User is redirected to the Login page |

### AUTH-06 – Forgot password flow

| Field | Value |
| --- | --- |
| Group | regression |
| Precondition | User is on the Login page |

| # | Step | Expected result |
| --- | --- | --- |
| 1 | Click **Forgot your password?** | Reset Password page opens |
| 2 | Enter a username and click **Reset Password** | Confirmation page states that a reset link has been sent |
| 3 | Click **Cancel** on the Reset Password page (separate run) | User returns to the Login page |

> **Not automated: step 2.** On the public demo the reset request returns `504 Gateway Time-out` after
> about 60 seconds, so the confirmation page cannot be verified there. It will be automated against the
> local Docker environment.

## 2. PIM – Employee Management

All employees are created by the test itself. Their last name is a unique marker (`Auto_<timestamp>_<random>`) and they are deleted after the test.

### PIM-01 – Add employee with required fields only

| Field | Value |
| --- | --- |
| Group | smoke |
| Precondition | Logged in as Admin |
| Test data | Random first name, unique last name, unique 10-digit Employee Id |

| # | Step | Expected result |
| --- | --- | --- |
| 1 | Open **PIM** from the side menu and click **Add** | Add Employee form is displayed |
| 2 | Enter First Name, Last Name and Employee Id | Values are displayed in the fields |
| 3 | Click **Save** | "Successfully Saved" toast; Personal Details page opens |
| 4 | Check Personal Details | First Name, Last Name and Employee Id match the entered data |

### PIM-02 – Add employee with a profile picture

| # | Step | Expected result |
| --- | --- | --- |
| 1 | Open Add Employee, enter required fields and upload `avatar.png` | Image preview is displayed |
| 2 | Click **Save** | Employee is saved; the profile picture is not the default image |

### PIM-03 – Add employee together with login details

| # | Step | Expected result |
| --- | --- | --- |
| 1 | Open Add Employee, enter required fields, enable **Create Login Details** | Username, Status, Password and Confirm Password fields appear |
| 2 | Enter a unique username and a valid password, click **Save** | Employee is saved |
| 3 | Log out and log in with the new credentials | Dashboard opens for the new user |

### PIM-04 – Required field and length validation

| # | Data | Expected result |
| --- | --- | --- |
| 1 | First Name empty | "Required" under First Name |
| 2 | Last Name empty | "Required" under Last Name |
| 3 | First Name longer than 30 characters | "Should not exceed 30 characters" |

All messages are verified in one run (soft assertions); the employee is not saved.

### PIM-05 – Duplicate Employee Id

| # | Step | Expected result |
| --- | --- | --- |
| 1 | Create employee A | Saved |
| 2 | Open Add Employee and enter the Employee Id of A | "Employee Id already exists" under Employee Id; Save does not create a record |

### PIM-06 – Search employee by name using autocomplete

| # | Step | Expected result |
| --- | --- | --- |
| 1 | Type the unique last name in **Employee Name** | Suggestion list shows the employee |
| 2 | Select the suggestion and click **Search** | Exactly one record, matching the employee |

### PIM-07 – Search by Employee Id and filter by Employment Status

| # | Step | Expected result |
| --- | --- | --- |
| 1 | Enter the Employee Id of a created employee and click **Search** | Exactly one record with that Id |
| 2 | Reset, select an **Employment Status** and click **Search** | Every listed record has the selected status |

### PIM-08 – Edit Personal Details

| # | Step | Expected result |
| --- | --- | --- |
| 1 | Open Personal Details of a created employee and record the current values | — |
| 2 | Change Middle Name, Date of Birth and Gender, click **Save** | "Successfully Updated" toast |
| 3 | Reload the page | New values are displayed; untouched fields keep their previous values |

### PIM-09 – Delete an employee from its table row

| # | Step | Expected result |
| --- | --- | --- |
| 1 | Create employees A and B sharing the same last name marker | Saved |
| 2 | Search by the marker | Two records are listed |
| 3 | Click the delete icon in the row of A and confirm | "Successfully Deleted" toast |
| 4 | Check the table | A is no longer listed; B is still listed |

### PIM-10 – Delete several employees using checkboxes

| # | Step | Expected result |
| --- | --- | --- |
| 1 | Create three employees sharing a marker and search by it | Three records are listed |
| 2 | Select two rows and click **Delete Selected**, confirm | Row count drops to one; the remaining row is the unselected employee |

### PIM-11 – Sort by name

| # | Step | Expected result |
| --- | --- | --- |
| 1 | Sort **First (& Middle) Name** ascending | Names on the page are in ascending order |
| 2 | Sort descending | Names on the page are in descending order |

### PIM-12 – Pagination

| # | Step | Expected result |
| --- | --- | --- |
| 1 | Read the total record count | — |
| 2 | Visit every page and count the rows | Sum of rows equals the total record count |

### PIM-13 – Create employees from an Excel file

| # | Step | Expected result |
| --- | --- | --- |
| 1 | For each row of `employees.xlsx`, create the employee | Each one is saved and can be found by Employee Id |

## 3. Dashboard and Navigation

### DASH-01 – Dashboard widgets

| # | Step | Expected result |
| --- | --- | --- |
| 1 | Log in as Admin | Dashboard shows all expected widgets |

### DASH-02 – Filter the side menu

| Field | Value |
| --- | --- |
| Group | regression |
| Precondition | Logged in as Admin |

| # | Step | Expected result |
| --- | --- | --- |
| 1 | Type a search term in the side menu search box | Only menu items containing the term (case-insensitive) are displayed |
