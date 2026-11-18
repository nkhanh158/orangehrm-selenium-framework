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
| 6 | Username with leading/trailing spaces | Recorded behaviour of the application |
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
| 2 | Click the browser **Back** button | User stays on (or is redirected to) the Login page; internal pages are not accessible |

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
