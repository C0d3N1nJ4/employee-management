package com.naiomi.employee.data.mapper;

import com.naiomi.employee.data.dto.EmployeeApiRequestDto;
import com.naiomi.employee.data.dto.EmployeeApiResponseDto;
import com.naiomi.employee.data.model.Employee;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.ReportingPolicy;

/**
 * Mapper interface for converting between {@link Employee} entities and their corresponding DTOs.
 * Utilizes MapStruct for automatic field mapping and customization of specific mappings.
 */
@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface EmployeeMapper {

    /**
     * Converts an {@link Employee} entity to an {@link EmployeeApiResponseDto}.
     *
     * @param employee the Employee entity to be converted
     * @return the mapped EmployeeApiResponseDto object
     */
    @Mapping(target = "name", expression = "java(employee.getFirstname() + ' ' + employee.getSurname())")
    @Mapping(target = "roleId", source = "role.id")
    EmployeeApiResponseDto toResponseDto(Employee employee);

    /**
     * Updates an existing {@link Employee} entity with the values from an {@link EmployeeApiRequestDto}.
     *
     * <p>Handles only the fields explicitly mapped. The role field is ignored, ensuring
     * that it is not accidentally overwritten.</p>
     *
     * <p>This method is particularly useful for partial updates where only certain fields
     * of an Employee are being modified.</p>
     *
     * @param requestDto the source EmployeeApiRequestDto containing the updated values
     * @param employee   the target Employee entity to be updated
     */
    @Mapping(target = "role", ignore = true)
    default void updateEmployeeFromRequestDto(EmployeeApiRequestDto requestDto, @MappingTarget Employee employee) {
        if (requestDto.getName() != null && !requestDto.getName().isBlank()) {
            // Split the name into first name and surname
            String[] nameParts = requestDto.getName().split(" ", 2);
            employee.setFirstname(nameParts[0]); // Set the first name
            employee.setSurname(nameParts.length > 1 ? nameParts[1] : ""); // Set the surname if available
        }
    }
}
