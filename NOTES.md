# Patch notes

## Summary of changes

- Corrected the task search predicate in the Spring Data query and both SQL reference files. Search terms, archive state, and status are now combined with the intended `AND`/`OR` grouping.
- Removed artificial request sleeping from the API and added validation for status, page, and page size.
- Made out-of-range pages return an empty result safely instead of risking an invalid sub-list range.
- Reset pagination when search or status filters change.
- Prevented stale requests from overwriting newer results and cleared stale errors when a request succeeds.
- Added an accessible label to the status select.

## Not changed

There is no role field or role vocabulary in the current schema, API, or seed data. I kept the existing status selector rather than inventing a separate role model or incorrectly treating assignees as roles.

## Biggest remaining risk

The API still loads every matching task before slicing the requested page. This is acceptable for the seeded exercise data, but production-sized datasets should use database-level pagination and a count query.

## Tools used

I used the repository README, source inspection, the Maven wrapper, npm, and the browser-facing API contract to reproduce and validate the changes.
