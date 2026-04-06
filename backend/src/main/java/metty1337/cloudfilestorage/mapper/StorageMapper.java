package metty1337.cloudfilestorage.mapper;

import metty1337.cloudfilestorage.dto.request.FileUploadData;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface StorageMapper {

//    default FileUploadData toFileUploadData(MultipartFile file) {
//        try {
//            return new FileUploadData(file.getOriginalFilename(), file.getInputStream(), file.getSize(), file.getContentType());
//        } catch (IOException e) {
//            throw new UncheckedIOException(e);
//        }
//    }
//
//    default List<FileUploadData> toFileUploadDataList(List<MultipartFile> files) {
//        return files.stream().map(this::toFileUploadData).collect(Collectors.toList());
//    }

    List<FileUploadData> toFileUploadData(List<MultipartFile> multipartFiles) throws IOException;

    @Mapping(target = "content", source = "inputStream")
    @Mapping(target = "filename", source = "originalFilename")
    FileUploadData toFileUploadData(MultipartFile file) throws IOException;
}
