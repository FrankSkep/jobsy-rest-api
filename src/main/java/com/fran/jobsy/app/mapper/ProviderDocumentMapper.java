package com.fran.jobsy.app.mapper;

import com.fran.jobsy.app.dto.providerrequest.ProviderDocumentResponse;
import com.fran.jobsy.app.entity.ProviderDocument;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface ProviderDocumentMapper {
    ProviderDocumentResponse toDto(ProviderDocument doc);

    List<ProviderDocumentResponse> toDtoList(List<ProviderDocument> docs);
}
