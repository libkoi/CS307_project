-- Provided Operations:
-- 1. 查余票
-- input (city A, city B, date D,current time T)
-- output(train_number_name,departure_time,arrival_time,station_ticket_left)

select f1.train_number_name,
       f1.STATION_NAME,
       f1.departure_time,
       f2.STATION_NAME,
       f2.arrival_time,
       f2.day_num-f1.day_num as day_cross,
       f1.station_ticket_left
from (
      (select STATION_NAME, station_to_id, t_n.TRAIN_NUMBER_ID, TRAIN_NUMBER_NAME, DEPARTURE_TIME,s_t_n.day_num station_ticket_left
       from city ci
              join station st on st.city_id = ci.city_id
              join _station_train_number s_t_n on s_t_n.station_id = st.station_id
              join train_number t_n on t_n.train_number_id = s_t_n.train_number_id
              join specific_train_number sp on sp.train_number_id = t_n.train_number_id
              join train_calendar t_c on t_c.train_calendar_id = sp.train_calendar_id
              join ticket ti
                   on ti.specific_train_number_id = sp.specific_train_number_id and ti.STATION_FROM_ID = st.STATION_ID
       where city_name = '深圳'
         and DATE(calendar) = '2019-05-21') f1
       join
     (select STATION_NAME, st.STATION_ID, t_n.TRAIN_NUMBER_ID, s_t_n.day_num,ARRIVAL_TIME
      from city ci
             join station st on st.city_id = ci.city_id
             join _station_train_number s_t_n on s_t_n.station_id = st.station_id
             join train_number t_n on t_n.train_number_id = s_t_n.train_number_id
             join specific_train_number sp on sp.train_number_id = t_n.train_number_id
             join train_calendar t_c on t_c.train_calendar_id = sp.train_calendar_id
      where city_name = '深圳'
        and DATE(calendar) = '2019-05-21'
     ) f2
     on f2.train_number_id = f1.train_number_id and f2.station_id = f1.station_to_id)
where TIME(f1.departure_time) > TIME('12:00')
order by f1.departure_time, f2.ARRIVAL_TIME desc;

-- 2. 买票
-- input(user_id,passengrt_id,ticket_id)
-- output(ticket表里票数减一     和     向purchased_order表里插入一条数据)
-- 需要先判断是否插入成功再执行insert语句
  update ticket set station_ticket_left=station_ticket_left-1 where ticket_id='ticket_id'and station_ticket_left>0 ;
  insert into purchased_order (user_id,passenger_id,ticket_id,order_price) values ('user_id','passenger_id','ticket_id');
  

-- 3. 退票
-- input（purchased_order_id）
-- output(ticket表里票数加一 然后 删去purchased_order表中对应数据)

update ticket set station_ticket_left=station_ticket_left+1 where ticket_id=(select ticket_id from purchased_order where purchased_order_id='purchased_order_id');
delete from purchased_order where purchased_order_id='purchased_order_id';
-- 4. 查套餐
-- input(none)
-- output(combo_id,combo_name)

select * from combo_menu;

-- 5. 订餐
-- input(user_id,purchased_order_id,combo_id)
insert into dining_order (user_id,purchased_order_id) values('user_id','purchased_order_id');
insert into _dining_order_combo (dining_order_id,combo_id) values((select dining_order_id from dining_order where user_id='user_id' and purchased_order_id='purchased_order_id'),'combo_id');
    
-- 6.退餐
-- input(dining_order_id)
delete from _dining_order_combo where dining_order_id='dining_order_id';
delete from dining_order where dining_order_id='dining_order_id';
-- 7.查线路时刻表
-- input(train_number_name)
-- output(station_name,arrival_time,departure_time)
select station_name,arrival_time,departure_time from station st join _station_train_number s_t_n on s_t_n.station_id=st.station_id join train_number t_n on t_n.train_number_id=s_t_n.train_number_id where train_number_name='train_number_name' order by station_order;
-- 8. 车站查票
-- input(station_name A, station_name B, date D, current_time T)
-- output(train_number_name,departure_time,arrival_time,station_ticket_left)
select f1.train_number_name,
       f1.STATION_NAME,
       f1.departure_time,
       f2.STATION_NAME,
       f2.arrival_time,
         f2.day_num-f1.day_num as day_cross,
       f1.station_ticket_left
from (
      (select STATION_NAME, station_to_id, t_n.TRAIN_NUMBER_ID, TRAIN_NUMBER_NAME, DEPARTURE_TIME,s_t_n.day_num, station_ticket_left
       from  station st 
              join _station_train_number s_t_n on s_t_n.station_id = st.station_id
              join train_number t_n on t_n.train_number_id = s_t_n.train_number_id
              join specific_train_number sp on sp.train_number_id = t_n.train_number_id
              join train_calendar t_c on t_c.train_calendar_id = sp.train_calendar_id
              join ticket ti
                   on ti.specific_train_number_id = sp.specific_train_number_id and ti.STATION_FROM_ID = st.STATION_ID
       where station_name = '深圳北'
         and DATE(calendar) = '2019-05-21') f1
       join
     (select STATION_NAME, st.STATION_ID, t_n.TRAIN_NUMBER_ID, ARRIVAL_TIME,s_t_n.day_num
      from  station st 
             join _station_train_number s_t_n on s_t_n.station_id = st.station_id
             join train_number t_n on t_n.train_number_id = s_t_n.train_number_id
             join specific_train_number sp on sp.train_number_id = t_n.train_number_id
             join train_calendar t_c on t_c.train_calendar_id = sp.train_calendar_id
      where station_name = '深圳北'
        and DATE(calendar) = '2019-05-21'
     ) f2
     on f2.train_number_id = f1.train_number_id and f2.station_id = f1.station_to_id)
where TIME(f1.departure_time) > TIME('12:00')
order by f1.departure_time, f2.ARRIVAL_TIME desc;

-- 9.买票查票
-- input(station_name A, station_name B, date D,train_number_name)
-- output(ticket_id,station_ticket_left)
select ticket_id,station_ticket_left from train_number t_n join specific_train_number sp on sp.train_number_id = t_n.train_number_id
             join train_calendar t_c on t_c.train_calendar_id = sp.train_calendar_id
             join ticket ti
                   on ti.specific_train_number_id = sp.specific_train_number_id where t_n.train_number_name='train_number_name'
					and DATE(calendar)='D' and station_from_id=(select station_id from station where station_name='A') and station_to_id=
                    (select station_id from station where station_name='B');
                    
-- 10 删线路

-- 11 根据日期得出星期几  triger
create trigger get_weekday
before insert on train_calendar
for each row
begin
    set  new.DAY_OF_WEEK=(select weekday(new.CALENDAR)+1);
end;


