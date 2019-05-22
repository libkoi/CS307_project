create trigger get_weekday
  before insert
  on train_calendar
  for each row
begin
  set NEW.DAY_OF_WEEK = (select weekday(new.CALENDAR) + 1);
end;