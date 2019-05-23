-- 1. 在specific_train_number 的train_number_id 上建立索引
create index s_t_n_index1
on specific_train_number (train_number_id);

-- 2.在specific_train_number 的train_calendar_id 上建立索引
create index s_t_n_index2
on specific_train_number (train_calendar_id);

-- 3. 在ticket 的station_from_id建立索引
create index ticket_index1 on ticket(station_from_id);
-- 4. 在ticket 的station_to_id建立索引
create index ticket_index2 on ticket(station_to_id);

-- 5. 在train_calendar的calendar 建立索引
create index t_a_index1 on train_calendar(calendar);

-- 6. 在city 的city_name 建立索引
create index city_index1 on city(city_name);
-- 7. 在purchased_order的ticket_id建立索引
create index p_o_index1 on purchased_order(ticket_id);