---
-- #%L
-- JSQLParser library
-- %%
-- Copyright (C) 2004 - 2019 JSQLParser
-- %%
-- Dual licensed under GNU LGPL 2.1 or Apache License 2.0
-- #L%
---
with
  org_chart (eid, emp_last, mgr_id, reportlevel, salary, job_id) as
  (
    (select employee_id, last_name, manager_id, 0 reportlevel, salary, job_id
    from employees
    where manager_id is null)
  union all
    (select e.employee_id, e.last_name, e.manager_id,
           r.reportlevel+1 reportlevel, e.salary, e.job_id
    from org_chart r, employees e
    where r.eid = e.manager_id)
  )
  search depth first by emp_last set order1
select lpad(' ',2*reportlevel)||emp_last emp_name, eid, mgr_id, salary, job_id
from org_chart
order by order1



--@FAILURE: Encountered unexpected token: "search" <S_IDENTIFIER> recorded first on Aug 3, 2021, 7:20:07 AM