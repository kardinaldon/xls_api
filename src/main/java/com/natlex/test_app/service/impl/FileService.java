package com.natlex.test_app.service.impl;

import com.natlex.test_app.model.entity.FileJob;
import com.natlex.test_app.model.entity.Geological;
import com.natlex.test_app.model.entity.Section;
import com.natlex.test_app.model.enumeration.FileJobStatus;
import com.natlex.test_app.model.enumeration.FileJobType;
import com.natlex.test_app.model.exception.FileHandlingException;
import com.natlex.test_app.model.exception.FileStorageException;
import com.natlex.test_app.service.IFileJobService;
import com.natlex.test_app.service.IFileService;
import com.natlex.test_app.service.ISectionService;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.io.*;
import java.net.MalformedURLException;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;

@Service
public class FileService implements IFileService {
    private static final Logger LOG = LoggerFactory.getLogger(FileService.class);
    @Value("${values.input_folder}")
    private Path inputFolder;
    @Value("${values.output_folder}")
    private Path outputFolder;
    @Value("${values.table_header}")
    private List<String> tableHeader;
    @Autowired
    private ISectionService sectionService;
    @Autowired
    private IFileJobService fileJobService;

    @Override
    @Async
    public void store(byte[] bytes, long fileJobId) {
        Optional<FileJob> fileJobOptional = fileJobService.getFileJob(fileJobId);
        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd-MM-yyyy_HH-mm-ss-SSS");
        LocalDateTime timeNow = LocalDateTime.now();
        String filename = dateFormatter.format(timeNow) + ".xls";
        try (FileOutputStream fos = new FileOutputStream(inputFolder.resolve(filename).toFile())) {
            fos.write(bytes);
        } catch (IOException e) {
            if (fileJobOptional.isPresent()) {
                FileJob fileJob = fileJobOptional.get();
                fileJob.setStatus(FileJobStatus.ERROR);
                fileJob.setType(FileJobType.IMPORT);
                fileJob.setMessage("error writing " + inputFolder.resolve(filename) + " file");
                fileJobService.save(fileJob);
            }
            LOG.error("error writing " + inputFolder.resolve(filename) + " file", e.getMessage());
            throw new FileStorageException("error writing " + inputFolder.resolve(filename) + " file");
        }
        readFromExcel(filename, fileJobOptional);
    }

    private void readFromExcel(String fileNameWithoutPath, Optional<FileJob> fileJobOptional) {
        List<Section> sectionList = new ArrayList<>();
        Section section = null;
        List<Geological> geologicalList;
        Geological geological = null;
        try (Workbook myExcelBook
                     = new HSSFWorkbook(
                new FileInputStream(inputFolder + "/" + fileNameWithoutPath))) {
            Sheet sheet = myExcelBook.getSheetAt(0);
            Iterator<Row> iterator = sheet.iterator();
            no_more_sections:
            while (iterator.hasNext()) {
                Row currentRow = iterator.next();
                if (currentRow.getRowNum() == 0) {
                    continue;
                }
                section = new Section();
                geologicalList = new ArrayList<>();
                Iterator<Cell> cellIterator = currentRow.iterator();
                while (cellIterator.hasNext()) {
                    Cell currentCell = cellIterator.next();
                    if (currentCell.getColumnIndex() == tableHeader.size()) {
                        break;
                    }
                    if(currentCell.getColumnIndex() == 0 && currentCell.getStringCellValue().isBlank()){
                        break no_more_sections;
                    }
                    if (currentCell.getColumnIndex() == 0) {
                        section.setName(currentCell.getStringCellValue());
                    }
                    else {
                        if (currentCell.getColumnIndex() % 2 == 0) {
                            if(!currentCell.getStringCellValue().isBlank()){
                                geological.setCode(currentCell.getStringCellValue());
                            }
                            else {
                                geological.setCode("");
                            }
                            geologicalList.add(geological);
                            geological = null;
                        } else {
                            geological = new Geological();
                            if(!currentCell.getStringCellValue().isBlank()){
                                geological.setName(currentCell.getStringCellValue());
                            }
                            else {
                                geological.setName("");
                            }
                        }
                    }
                }
                section.setGeologicalClasses(geologicalList);
                sectionList.add(section);
            }
            sectionService.saveAll(sectionList);
            if (fileJobOptional.isPresent()) {
                FileJob fileJob = fileJobOptional.get();
                fileJob.setType(FileJobType.IMPORT);
                fileJob.setStatus(FileJobStatus.DONE);
                fileJob.setFilePath(inputFolder.resolve(fileNameWithoutPath).toString());
                fileJobService.save(fileJob);
            }
        } catch (IOException e) {
            if (fileJobOptional.isPresent()) {
                FileJob fileJob = fileJobOptional.get();
                fileJob.setType(FileJobType.IMPORT);
                fileJob.setStatus(FileJobStatus.ERROR);
                fileJob.setMessage(" error reading " + inputFolder + "/" + fileNameWithoutPath + " file");
                fileJobService.save(fileJob);
            }
            LOG.error(" error reading " + inputFolder + "/" + fileNameWithoutPath + " file", e.getMessage());
            throw new FileHandlingException("file read error");
        }
    }

    @Override
    @Async
    public void exportFromDbToFile(FileJob fileJob) {
        List<Section> sectionList = sectionService.getAll();
        if (!sectionList.isEmpty()) {
            int rowIndex = 0;
            int cellIndex = 0;
            Workbook book = new HSSFWorkbook();
            Sheet sheet = book.createSheet();
            Row headerRow = sheet.createRow(rowIndex);
            for (String value : tableHeader) {
                Cell cell = headerRow.createCell(cellIndex);
                cell.setCellValue(value);
                cellIndex++;
            }
            rowIndex = 1;
            for (Section section : sectionList) {
                cellIndex = 0;
                Row row = sheet.createRow(rowIndex);
                Cell sectionNameCell = row.createCell(cellIndex);
                sectionNameCell.setCellValue(section.getName());
                cellIndex++;
                List<Geological> geologicalClasses = section.getGeologicalClasses();
                if (!geologicalClasses.isEmpty()) {
                    for (Geological geological : geologicalClasses) {
                        Cell geologicalCell = row.createCell(cellIndex);
                        geologicalCell.setCellValue(geological.getName());
                        cellIndex++;
                        geologicalCell = row.createCell(cellIndex);
                        geologicalCell.setCellValue(geological.getCode());
                        cellIndex++;
                    }
                }
                rowIndex++;
            }
            DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd-MM-yyyy_HH-mm-ss-SSS");
            LocalDateTime timeNow = LocalDateTime.now();
            try (FileOutputStream fileOutputStream = new FileOutputStream(outputFolder
                    + "/" + dateFormatter.format(timeNow) + ".xls")) {
                book.write(fileOutputStream);
                book.close();
                fileJob.setStatus(FileJobStatus.DONE);
                fileJob.setType(FileJobType.EXPORT);
                fileJob.setFilePath(outputFolder.resolve(dateFormatter.format(timeNow)).toString() + ".xls");
                fileJobService.save(fileJob);
            } catch (IOException e) {
                fileJob.setType(FileJobType.EXPORT);
                fileJob.setStatus(FileJobStatus.ERROR);
                fileJob.setMessage(" error writing " + outputFolder
                        + "/" + dateFormatter.format(timeNow) + ".xls" + " file");
                fileJobService.save(fileJob);
                LOG.error(" error writing " + outputFolder
                        + "/" + dateFormatter.format(timeNow) + ".xls" + " file", e.getMessage());
                throw new FileStorageException(" error writing " + outputFolder
                        + "/" + dateFormatter.format(timeNow) + ".xls" + " file");
            }
        }
    }

    @Override
    public Resource getByJobID(FileJob fileJob) throws MalformedURLException {
        Path filePath = Path.of(fileJob.getFilePath()).normalize();
        Resource resource = new UrlResource(filePath.toUri());
        if (resource.exists()) {
            return resource;
        } else {
            LOG.error(" file " + fileJob.getFilePath() + " not found");
            throw new FileStorageException(" file " + fileJob.getFilePath() + " not found");
        }
    }
}
