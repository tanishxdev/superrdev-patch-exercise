# Patch Notes

## What I changed

I focused on correctness and the most visible user-facing issues:

- Fixed the search query in the Spring repository and both SQL reference files. Parentheses now ensure that archived tasks and tasks with the wrong status cannot bypass the filters because of SQL `AND`/`OR` precedence.
- Removed the artificial delay in the API and added validation for status, page, and page size.
- Made requests for pages beyond the result set return an empty list safely.
- Reset pagination to page one when the search text or status filter changes.
- Prevented slower, outdated requests from replacing newer results and cleared stale errors on a successful request.
- Added an accessible label to the status selector.

## What I chose not to change

The project has a status field (`OPEN`, `IN_PROGRESS`, and `DONE`) and an assignee field, but no role column, role values, or role API contract. I therefore kept the existing status selector instead of inventing a role model that would not be supported by the backend.

## Remaining risk

The API still loads all matching tasks before applying pagination in memory. That is adequate for the sample data, but a production system should use database-level pagination and a separate count query.

## Assumptions

- The existing status filter is the intended selector because the schema and API define statuses, not user roles.
- Invalid status, page, or page-size values should receive a clear `400 Bad Request` response rather than being silently corrected.
- A page beyond the final page should return no items, which is safer and more predictable than failing the request.
- The seeded task data is representative enough for this exercise; database-level pagination is noted as future production work.

## Validation

The frontend production build passed. The backend Maven build passed; the project currently contains no automated tests. I also checked the changed source files for diagnostics and whitespace errors.

## Tools used

I used the README, source inspection, the Maven wrapper, npm, SQL review, and the API contract to investigate and validate the patch.
