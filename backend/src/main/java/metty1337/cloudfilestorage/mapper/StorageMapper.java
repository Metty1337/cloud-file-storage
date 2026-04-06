package metty1337.cloudfilestorage.mapper;

import metty1337.cloudfilestorage.constants.ObjectType;
import metty1337.cloudfilestorage.dto.request.storage.FileUploadData;
import metty1337.cloudfilestorage.dto.response.storage.StorageDirectoryResponse;
import metty1337.cloudfilestorage.dto.response.storage.StorageFileResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface StorageMapper {

    List<FileUploadData> toFileUploadData(List<MultipartFile> multipartFiles) throws IOException;

    @Mapping(target = "content", source = "inputStream")
    @Mapping(target = "filename", source = "originalFilename")
    FileUploadData toFileUploadData(MultipartFile file) throws IOException;

    StorageFileResponse toStorageFileResponse(String path, String name, long size, ObjectType type);

    StorageDirectoryResponse toStorageDirectoryResponse(String path, String name, ObjectType type);

}
