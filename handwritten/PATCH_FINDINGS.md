# Task Tracker — Patch Findings

This document summarizes the bugs identified during the review of the Task Tracker application, their root causes, implemented fixes, and verification results.

---

## 1. Search Filter Bug

**Location**

`backend/src/main/java/com/internal/tasktracker/TaskRepository.java`

### How I Found It

I traced the search request from the frontend to the repository SQL query. The query used `AND` and `OR` conditions without grouping the search conditions with parentheses.

### Root Cause

SQL evaluates `AND` before `OR`.

Because of this operator precedence, a task whose **title matched the search query** could bypass the `archived` and `status` conditions.

### Fix

I grouped the title and description search conditions:

```sql
WHERE archived = false
  AND status = :status
  AND (
      title ILIKE :query
      OR description ILIKE :query
  )
```

This ensures that the archived and status conditions apply to both the title and description search conditions.

---

## 2. Pagination Validation Bug

**Location**

`backend/src/main/java/com/internal/tasktracker/TaskController.java`

### How I Found It

I reviewed how `page` and `pageSize` were used to calculate list indexes and pagination ranges.

### Root Cause

Invalid pagination values such as:

* `page = 0`
* negative page values
* `pageSize < 1`
* excessively large `pageSize` values

could result in invalid pagination ranges or unsafe index calculations.

### Fix

I added validation requiring:

```text
page >= 1
pageSize >= 1
pageSize <= 100
```

I also handled requests for pages beyond the available results safely by returning an empty list instead of producing an invalid range.

---

## 3. Filter and Stale-Request Bug

**Location**

* `frontend/src/App.jsx`
* `frontend/src/hooks/useTasks.js`

### How I Found It

When changing the search or status filter while viewing a later page, the previous page number remained active.

### Root Cause

A new filter could produce fewer available pages, while the UI continued requesting the previously selected page.

There was also a race condition where an older, slower request could finish after a newer request and overwrite the newer results.

### Fix

I made two changes:

1. Reset pagination to **page 1** whenever the search or status filter changes.
2. Ignore responses from cancelled or outdated requests.

This keeps the displayed results consistent with the latest filter state.

---

## 4. Accessibility Improvement

**Location**

`frontend/src/components/StatusFilter.jsx`

### Issue

The status `<select>` element did not have a clear accessible label.

### Fix

I added a visible **Status** label and an `aria-label` describing the selector.

This improves the usability of the filter for users relying on assistive technologies.

---

# Verification

The following checks were completed successfully:

* [x] Frontend production build completed successfully.
* [x] Backend Maven command completed successfully.
* [x] Maven reported no automated backend tests because the project currently has no backend test cases.
* [x] Backend build completed successfully despite there being no tests to run.
* [x] Changed files were checked for diagnostics errors.
* [x] Changed files were checked for formatting issues.

## Summary

The patch addresses four areas:

| Area          | Issue                            | Fix                                           |
| ------------- | -------------------------------- | --------------------------------------------- |
| Search        | `AND` / `OR` precedence          | Grouped search conditions                     |
| Pagination    | Invalid page ranges              | Added input validation and safe empty results |
| Filtering     | Stale page and request results   | Reset page and ignore outdated requests       |
| Accessibility | Unclear status selector labeling | Added visible label and `aria-label`          |

These changes improve the correctness, robustness, user experience, and accessibility of the Task Tracker application.
