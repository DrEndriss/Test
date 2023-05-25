package com.example.demo.handler;

import javafx.animation.PauseTransition;
import javafx.collections.ObservableList;
import javafx.scene.control.TableView;
import javafx.stage.FileChooser;
import javafx.util.Duration;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

public class DataHandler {
    public List<String[]> readDataFromCSV(String filePath) {
        List<String[]> dataList = new ArrayList<>();

        try (BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(filePath), StandardCharsets.UTF_8))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] row = line.split(";");
                dataList.add(row);
            }
        } catch (IOException e) {
            System.err.println("Error reading file: " + e.getMessage());
        }

        return dataList;
    }

    public boolean writeToCsv(List<String[]> allData, ObservableList<String[]> tableData, String filepath) {
        String delimiter = ";";
        File file = new File(filepath);

        try (PrintWriter writer = new PrintWriter(new OutputStreamWriter(new FileOutputStream(file), StandardCharsets.UTF_8))) {
            // Write the header row
            String[] headerRow = allData.get(0);
            for (int i = 0; i < headerRow.length; i++) {
                writer.print(headerRow[i]);
                if (i < headerRow.length - 1) {
                    writer.print(delimiter);
                }
            }
            writer.println();

            // Write the data rows, starting from index 1 to skip the first row
            for (int rowIndex = 1; rowIndex < tableData.size(); rowIndex++) {
                String[] row = tableData.get(rowIndex);
                StringBuilder rowBuilder = new StringBuilder();
                for (int i = 0; i < row.length; i++) {
                    rowBuilder.append(row[i]);
                    if (i < row.length - 1) {
                        rowBuilder.append(delimiter);
                    }
                }
                String rowString = rowBuilder.toString();
                writer.println(rowString);
            }

            System.out.println("Exported data to " + file.getAbsolutePath());
            return true;
        } catch (IOException ex) {
            System.err.println("Error exporting data to " + file.getAbsolutePath() + ": " + ex.getMessage());
            return false;
        }
    }

    public boolean exportToCSV(TableView<String[]> table, List<String[]> allData, ObservableList<String[]> tableData) {
        LoadingIndicator loadingIndicator = new LoadingIndicator();
        loadingIndicator.showLoadingIndicator(table.getScene());

        PauseTransition delay = new PauseTransition(Duration.seconds(2));
        delay.setOnFinished(event -> {
            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Save CSV File");
            fileChooser.setInitialFileName("data.csv");
            fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("CSV Files", "*.csv"));
            File file = fileChooser.showSaveDialog(table.getScene().getWindow());

            if (file != null) {
                try (PrintWriter writer = new PrintWriter(file, StandardCharsets.UTF_8)) {
                    String[] headerRow = allData.get(0);
                    for (int i = 0; i < headerRow.length; i++) {
                        writer.print(headerRow[i]);
                        if (i < headerRow.length - 1) {
                            writer.print(";");
                        }
                    }
                    writer.println();

                    for (String[] row : tableData) {
                        for (int i = 0; i < row.length; i++) {
                            writer.print(row[i]);
                            if (i < row.length - 1) {
                                writer.print(";");
                            }
                        }
                        writer.println();
                    }

                    System.out.println("Exported data to " + file.getAbsolutePath());
                } catch (IOException ex) {
                    System.err.println("Error exporting data to " + file.getAbsolutePath() + ": " + ex.getMessage());
                } finally {
                    loadingIndicator.hideLoadingIndicator();
                }
            } else {
                System.out.println("Process canceled");
                loadingIndicator.hideLoadingIndicator();
            }
        });

        delay.play();
        return true;
    }

    public boolean exportToExcel(TableView<String[]> table, List<String[]> allData, ObservableList<String[]> tableData) {
        LoadingIndicator loadingIndicator = new LoadingIndicator();
        loadingIndicator.showLoadingIndicator(table.getScene());

        PauseTransition delay = new PauseTransition(Duration.seconds(2));
        delay.setOnFinished(event -> {
            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Save Excel File");
            fileChooser.setInitialFileName("data.xlsx");
            fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Excel Files", "*.xlsx"));
            File file = fileChooser.showSaveDialog(table.getScene().getWindow());

            if (file != null) {
                Workbook workbook = new XSSFWorkbook();
                Sheet sheet = workbook.createSheet("Data");

                try {
                    Row headerRow = sheet.createRow(0);
                    for (int i = 0; i < allData.get(0).length; i++) {
                        Cell cell = headerRow.createCell(i);
                        cell.setCellValue(allData.get(0)[i]);
                    }

                    for (int i = 0; i < tableData.size(); i++) {
                        Row dataRow = sheet.createRow(i + 1);
                        String[] rowData = tableData.get(i);
                        for (int j = 0; j < rowData.length; j++) {
                            Cell cell = dataRow.createCell(j);
                            cell.setCellValue(rowData[j]);
                        }
                    }

                    try (FileOutputStream outputStream = new FileOutputStream(file)) {
                        workbook.write(outputStream);
                        System.out.println("Exported data to " + file.getAbsolutePath());
                    } catch (IOException ex) {
                        System.err.println("Error exporting data to " + file.getAbsolutePath() + ": " + ex.getMessage());
                    } finally {
                        workbook.close();
                        loadingIndicator.hideLoadingIndicator();
                    }
                } catch (IOException ex) {
                    System.err.println("Error creating Excel workbook: " + ex.getMessage());
                    loadingIndicator.hideLoadingIndicator();
                }
            } else {
                System.out.println("Process canceled");
                loadingIndicator.hideLoadingIndicator();
            }
        });

        delay.play();
        return true;
    }

}