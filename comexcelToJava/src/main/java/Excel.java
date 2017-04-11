import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.*;

/**
 * Created by Administrator on 2017/3/23.
 */
public class Excel {

    public static void main(String[] args) {
        String fileReadName = "D:\\ewalletDatabase/test.xlsx";
        String fileWriteName = "D:\\ewalletDatabase/ewalletDatabase.sql";
        String context;
        // 检测代码
        try {
            Excel excel = new Excel();
            context = excel.readExcel(fileReadName);
            excel.contentToTxt(fileWriteName, context);
        } catch (Exception ex) {
        }
    }

    public String readExcel(String strPath) {
        // 构造 XSSFWorkbook 对象，strPath 传入文件路径
        XSSFWorkbook xwb = null;
        try {
            xwb = new XSSFWorkbook(new FileInputStream(new File(strPath)));
        } catch (IOException e) {
            e.printStackTrace();
        }
        // 读取第一章表格内容
        XSSFSheet sheet = xwb.getSheetAt(1);
        // 定义 row、cell
        XSSFRow row;
        // 循环输出表格中的内容
        // System.out.println(sheet.getLastRowNum());
        StringBuffer tables = new StringBuffer();
        tables.append("CREATE DATABASE `ewalletdb`;\n" +
                "USE `ewalletdb`;\n ");
        String table_name = "";
        for (int i = 0; i < sheet.getLastRowNum() + 1; i++) {
            row = sheet.getRow(i);
            // 通过 row.getCell(j).toString() 获取单元格内容，
            String xuhao = row.getCell(0).toString();
            String zh_name = row.getCell(1).toString();
            String en_name = row.getCell(2).toString();
            String type = row.getCell(3).toString();
            String primay = row.getCell(4).toString();
            String is_null = row.getCell(5).toString();
            String fal = row.getCell(6).toString();
            String remark = row.getCell(7).toString();

            // System.out.print(xuhao);
            // System.out.print(zh_name);
            // System.out.print(en_name);
            // System.out.println(type);
            // System.out.println(primay);
            // System.out.println(is_null);
            // System.out.println(fal);
            // System.out.println(remark);
            // System.out.print("\t");
            // System.out.println("");
            // System.out.println(sheet.getRow(i+1).getCell(0).toString());
            if (i > 1 && sheet.getRow(i).getCell(0).toString().equalsIgnoreCase("表中文名")) {
                tables.append("PRIMARY KEY (`id`)\n" + ") ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='" + table_name
                        + "';\n");
            }
            if (xuhao.equalsIgnoreCase("表中文名")) {
                tables.append("CREATE TABLE IF NOT EXISTS ");
                tables.append("`" + type + "` (\n");
                table_name = zh_name;
                continue;
            }
            if (xuhao.equalsIgnoreCase("序号")) {
                continue;
            }
            tables.append("`" + en_name + "` ");
            tables.append(" " + type + " ");
            tables.append(" " + is_null + " ");
            if (en_name.equalsIgnoreCase("id")) {
                tables.append(" AUTO_INCREMENT  ");
            }
            if (remark.equalsIgnoreCase("")) {
                tables.append(" COMMENT '" + zh_name + "',\n ");
            } else {
                tables.append(" COMMENT'" + remark + "',\n ");
            }
        }
        tables.append("PRIMARY KEY (`id`)\n" + ") ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='" + table_name + "';\n");
		System.out.println("生成完成");
        return tables.toString();
    }

    public void contentToTxt(String filePath, String content) {
//		String str = new String(); //原有txt内容
        String s1 = new String();//内容更新
        try {
            File f = new File(filePath);
            if (f.exists()) {
                System.out.print("文件存在");
            } else {
                System.out.print("文件不存在");
                f.createNewFile();// 不存在则创建
            }
//			BufferedReader input = new BufferedReader(new FileReader(f));
//
//			while ((str = input.readLine()) != null) {
//				s1 += str + "\n";
//			}
//			System.out.println(s1);
//			input.close();
            s1 += content;

            BufferedWriter output = new BufferedWriter(new FileWriter(f));
            output.write(s1);
            output.close();
            System.out.println("写入文件");
        } catch (Exception e) {
            e.printStackTrace();

        }
    }
}
