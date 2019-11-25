drop index testcases_ownership_unique;
alter table test_cases add constraint testcases_ownership_unique unique (primary_owner_id, test_class, test_method, project_id);