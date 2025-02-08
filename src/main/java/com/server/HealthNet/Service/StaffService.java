package com.server.HealthNet.Service;

import com.server.HealthNet.Model.Staff;
import com.server.HealthNet.Repository.StaffRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class StaffService {

    @Autowired
    private StaffRepository staffRepository;

    public Optional<Staff> getStaffById(Long staffId) {
        return staffRepository.findStaffById(staffId);
    }

    public List<Staff> getAllStaff() {
        return staffRepository.findAllStaff();
    }

    public int deleteStaffById(Long staffId) {
        return staffRepository.deleteStaffById(staffId);
    }

    public int updateStaff(Staff staff) {
        return staffRepository.updateStaff(staff);
    }

    public Long addStaff(Staff staff) {
        return staffRepository.saveStaff(staff);
    }
}
