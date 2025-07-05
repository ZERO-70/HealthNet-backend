package com.server.HealthNet.Service;

import com.server.HealthNet.Model.Department;
import com.server.HealthNet.Model.DepartmentSummaryDTO;
import com.server.HealthNet.Repository.DepartmentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.stream.Collectors;

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

    /**
     * Get all department summaries (ID and name only)
     * Lightweight method for dropdowns and selections
     */
    public List<DepartmentSummaryDTO> getAllDepartmentSummaries() {
        return departmentRepository.findAll().stream()
                .map(dept -> new DepartmentSummaryDTO(dept.getDepartment_id(), dept.getName()))
                .collect(Collectors.toList());
    }
}
