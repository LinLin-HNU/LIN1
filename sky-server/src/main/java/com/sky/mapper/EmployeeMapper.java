package com.sky.mapper;

import com.sky.annotation.AutoFill;
import com.sky.entity.Employee;
import com.sky.enumeration.OperationType;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface EmployeeMapper {

    /**
     * 根据用户名查询员工
     * @param username
     * @return
     */
    @Select("select * from employee where username = #{username}")
    Employee getByUsername(String username);

    /**
     * 插入员工数据
     */

    @Insert("insert into employee (username, name, password, phone, sex, id_number, status, create_time, update_time, create_user, update_user) " +
            "values (#{username}, #{name}, #{password}, #{phone}, #{sex}, #{idNumber}, #{status}, #{createTime}, #{updateTime}, #{createUser}, #{updateUser})")
    @AutoFill(value = OperationType.INSERT)
//    void insert(@Param("employee") Employee employee);
    void insert(Employee employee);

    @Select("<script>" +
            "select * from employee " +
            "<where>" +
            "<if test='name != null and name != \"\"'>" +
            "name like concat('%', #{name}, '%')" +
            "</if>" +
            "</where>" +
            "order by create_time desc" +
            "</script>")
    List<Employee> list(String name);

    @Select("SELECT * FROM employee WHERE id = #{id}")
    Employee getById(Long id);


    @AutoFill(value = OperationType.UPDATE)
    void update(Employee employee);

    void updatePassword(Employee employee);

    void updateStatus(Employee employee);
}
