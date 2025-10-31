package com.fran.jobsy.app.mapper;

import com.fran.jobsy.app.dto.providerrequest.MyProviderRequestResponse;
import com.fran.jobsy.app.dto.providerrequest.ProviderRequestMinResponse;
import com.fran.jobsy.app.dto.providerrequest.ProviderRequestResponse;
import com.fran.jobsy.app.entity.ProviderRequest;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring", uses = {UserMapper.class, ProviderDocumentMapper.class})
public interface ProviderRequestMapper {
    @Mapping(target = "user", source = "user")
    @Mapping(target = "status", expression = "java(pr.getStatus().name())")
    @Mapping(target = "documents", source = "documents")
    ProviderRequestResponse toDTO(ProviderRequest pr);

    @Mapping(target = "userName", expression = "java(pr.getUser().getFirstname() + \" \" + pr.getUser().getLastname())")
    @Mapping(target = "userPhoto", expression = "java(pr.getUser().getPhoto() != null ? pr.getUser().getPhoto().getUrl() : null)")
    @Mapping(target = "status", expression = "java(pr.getStatus().name())")
    ProviderRequestMinResponse toDTOMin(ProviderRequest pr);

    @Mapping(target = "status", expression = "java(pr.getStatus().name())")
    MyProviderRequestResponse toDTOMy(ProviderRequest pr);
}
