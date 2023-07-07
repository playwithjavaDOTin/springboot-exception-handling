package playwithjava.in.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import playwithjava.in.bean.request.EmployeeRequest;
import playwithjava.in.bean.response.EmployeeResponse;
import playwithjava.in.entity.EmployeeEntity;
import playwithjava.in.exception.DuplicateEntityException;
import playwithjava.in.exception.EntityNotFoundException;
import playwithjava.in.mapper.EmployeeMapperService;
import playwithjava.in.repository.EmployeeRepository;
import playwithjava.in.service.EmployeeService;

import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class EmployeeServiceImpl implements EmployeeService {

    @Autowired
    EmployeeRepository employeeRepository;

    @Autowired
    EmployeeMapperService mapperService;

    @Override
    public EmployeeResponse addEmployee(EmployeeRequest request) {

        EmployeeEntity employeeEntityData=employeeRepository.findByEmail(request.getEmail());
        if(employeeEntityData!=null){
            throw new DuplicateEntityException("Entity already available in system",409);
        }
        EmployeeEntity employeeEntity=mapperService.mapEmployeeReqToEntity(request);
        EmployeeEntity entity= employeeRepository.save(employeeEntity);
        EmployeeResponse employeeResponse=mapperService.mapEmployeeEntityToResponse(entity);
        return employeeResponse;
    }

    @Override
    public EmployeeResponse updateEmployee(EmployeeRequest request) {
        return null;
    }

    @Override
    public EmployeeResponse getEmployee(Long sid) {
        Optional<EmployeeEntity> employeeEntity=employeeRepository.findById(sid);

        if(!employeeEntity.isPresent()){
            throw  new EntityNotFoundException("Entity not found", HttpStatus.NOT_FOUND.value());
        }
        return mapperService.mapEmployeeEntityToResponse(employeeEntity.get());
    }

    @Override
    public List<EmployeeResponse> getAllEmployee() {

        List<EmployeeEntity> employeeEntities= employeeRepository.findAll();

        Function<EmployeeEntity,EmployeeResponse> convert=(x)->mapperService.mapEmployeeEntityToResponse(x);
        List<EmployeeResponse> responses=employeeEntities.stream()
                .map(convert)
                .collect(Collectors.toList());
        return responses;
    }

    @Override
    public void deleteEmployee(Long sid) {
        employeeRepository.deleteById(sid);
    }
}
