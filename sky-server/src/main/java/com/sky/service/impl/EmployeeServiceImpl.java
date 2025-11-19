package com.sky.service.impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.sky.constant.MessageConstant;
import com.sky.constant.PasswordConstant;
import com.sky.constant.StatusConstant;
import com.sky.context.BaseContext;
import com.sky.dto.EmployeeDTO;
import com.sky.dto.EmployeeLoginDTO;
import com.sky.dto.EmployeePageQueryDTO;
import com.sky.entity.Employee;
import com.sky.exception.AccountLockedException;
import com.sky.exception.AccountNotFoundException;
import com.sky.exception.PasswordErrorException;
import com.sky.mapper.EmployeeMapper;
import com.sky.result.PageResult;
import com.sky.result.Result;
import com.sky.service.EmployeeService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;


import java.time.LocalDateTime;

@Service
public class EmployeeServiceImpl implements EmployeeService {

    @Autowired
    private EmployeeMapper employeeMapper;

    /**
     * 员工登录
     *
     * @param employeeLoginDTO
     * @return
     */
    public Employee login(EmployeeLoginDTO employeeLoginDTO) {
        String username = employeeLoginDTO.getUsername();
        // 对前端传过来的密码进行MD5加密
        String password = DigestUtils.md5DigestAsHex(employeeLoginDTO.getPassword().getBytes());

        //1、根据用户名查询数据库中的数据
        Employee employee = employeeMapper.getByUsername(username);

        //2、处理各种异常情况（用户名不存在、密码不对、账号被锁定）
        if (employee == null) {
            //账号不存在
            throw new AccountNotFoundException(MessageConstant.ACCOUNT_NOT_FOUND);
        }

        //密码比对
        if (!password.equals(employee.getPassword())) {
            //密码错误
            throw new PasswordErrorException(MessageConstant.PASSWORD_ERROR);
        }

        if (employee.getStatus() == StatusConstant.DISABLE) {
            //账号被锁定
            throw new AccountLockedException(MessageConstant.ACCOUNT_LOCKED);
        }

        //3、返回实体对象
        return employee;
    }

    @Override
    public Result addEmployee(EmployeeDTO employeeDTO) {
        // 输出当前线程id
        System.out.println("Service线程id：" + Thread.currentThread().getId());
        Employee employee = new Employee();

//            employee.setIdNumber(employeeDTO.getIdNumber());
//            employee.setUsername(employeeDTO.getUsername());
//            employee.setName(employeeDTO.getName());
//            employee.setPhone(employeeDTO.getPhone());
//            employee.setSex(employeeDTO.getSex());
            // 以上代码冗余，可使用对象属性拷贝工具进行简化
            BeanUtils.copyProperties(employeeDTO,employee);

            employee.setStatus(StatusConstant.ENABLE); // 新增员工默认启用状态
            employee.setCreateTime(LocalDateTime.now());
            employee.setUpdateTime(LocalDateTime.now());


            employee.setCreateUser(BaseContext.getCurrentId());
            employee.setUpdateUser(BaseContext.getCurrentId());
            employee.setPassword(DigestUtils.md5DigestAsHex(PasswordConstant.DEFAULT_PASSWORD.getBytes())); // 默认密码123456，需要加密处理

            employeeMapper.insert(employee);
            return Result.success();
        }

    /**
     * 分页查询员工信息
      * @param employeePageQueryDTO
     * @return
     */
    @Override
    public PageResult getEmployees(EmployeePageQueryDTO employeePageQueryDTO) {
        // 开始分页查询 使用pageHelper插件
        // 告诉pageHelper页码和页面尺寸，pagehelper进行计算 ((页码-1)*页面尺寸，页面尺寸) 即为limit的参数
        // // 底层涉及到sql的动态拼接，利用ThreadLocal，
        // 将limit的两个参数存入到存储空间里面，到拼接sql时再拿出来用
        PageResult pageResult = new PageResult();
        PageHelper.startPage(employeePageQueryDTO.getPage(), employeePageQueryDTO.getPageSize());
        Page<Employee> page = employeeMapper.pageQuery(employeePageQueryDTO);
        pageResult.setTotal(page.getTotal());
        pageResult.setRecords(page.getResult());
        return pageResult;
    }
}

