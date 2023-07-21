package com.order.chandler.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.order.chandler.entity.Employee;
import com.order.chandler.mapper.EmployeeMapper;
import com.order.chandler.service.EmployeeService;
import org.springframework.stereotype.Service;

@Service
public class EmployeeServiceImpl extends ServiceImpl<EmployeeMapper, Employee> implements EmployeeService {
}
