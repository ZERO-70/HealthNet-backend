package com.server.HealthNet.Service;

import com.server.HealthNet.Model.Department;
import com.server.HealthNet.Repository.DepartmentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class DepartmentService {

    @Autowired
    private DepartmentRepository departmentRepository;

    public List<Department> getAllDepartments() {
        return departmentRepository.findAll();
    }

    public Department getDepartmentById(Long id) {
        return departmentRepository.findById(id);
    }

    public int createDepartment(Department department) {
        return departmentRepository.save(department);
    }

    public int updateDepartment(Department department) {
        return departmentRepository.update(department);
    }

    public int deleteDepartment(Long id) {
        return departmentRepository.deleteById(id);
    }
}
