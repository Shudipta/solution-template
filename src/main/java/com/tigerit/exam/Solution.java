package com.tigerit.exam;


//import static com.tigerit.exam.IO.*;
import java.util.*;
import java.io.*;

/**
 * All of your application logic should be placed inside this class.
 * Remember we will load your application from our custom container.
 * You may add private method inside this class but, make sure your
 * application's execution points start from inside run method.
 */
public class Solution implements Runnable {
    @Override
    public void run() {
        // your application entry point

        // // sample input process
        InputReader in = new InputReader(System.in);
        /*------------------------------My Code starts here------------------------------*/

        int totalTestCase;
        totalTestCase = in.nextInt();
        for(int nCase = 1; nCase <= totalTestCase; nCase++)
        {
            println("Test: " + nCase);
            
            int nT;
            nT = in.nextInt();
            
            Table[] tables = new Table[nT];
            Map<String, Integer> tableId = new HashMap<>();
            for(int i = 0; i < nT; i++)
            {
                tables[i] = new Table(in);
                tableId.put(tables[i].tName, i);
            }
            
            int nQ = in.nextInt();
            while(nQ > 0)
            {
                Query q = new Query(in);
                q.exec(tables, tableId);
                
                nQ--;
            }
        }

        /*------------------------------The End------------------------------------------*/
    }

    public static void print(Object o){
        System.out.print(o);
    }
    
    public static void println(Object o){
        System.out.println(o);
    }

    static class InputReader {
        public BufferedReader reader;
        public StringTokenizer tokenizer;

        public InputReader(InputStream inputstream) {
            reader = new BufferedReader(new InputStreamReader(inputstream));
            tokenizer = null;
        }

        public String nextLine() {
            String fullLine = null;
            while (tokenizer == null || !tokenizer.hasMoreTokens()) {
                try {
                    fullLine = reader.readLine();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
                return fullLine.trim();
            }
            return fullLine.trim();
        }

        public String next() {
            while (tokenizer == null || !tokenizer.hasMoreTokens()) {
                try {
                    tokenizer = new StringTokenizer(reader.readLine());
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
            return tokenizer.nextToken();
        }

        public int nextInt() {
            return Integer.parseInt(next());
        }
    }

    class Table {
        String tName;
        int nC, nD;
        String[] cName;
        int[][] data;
        Map<String, Integer> colId = new HashMap<>();
        
        public Table(InputReader in) {
            tName = in.nextLine();
            nC = in.nextInt();
            nD = in.nextInt();
            cName = new String[nC];
            data = new int[nD][nC];

            for(int i = 0; i < nC; i++)
            {
                cName[i] = in.next();
                colId.put(cName[i], i);
            }

            for(int i = 0; i < nD; i++)
                for(int j = 0; j < nC; j++)
                    data[i][j] = in.nextInt();
        }
    }
    
    class Query {
        String part;
        List<String>[] Parts;
        StringTokenizer tokenizer;
        String tName1, tName2, tShortName1, tShortName2, colName1, colName2;
        int type, table1, table2, predicateCol1, predicateCol2;
        List<Integer> whichTable;
        List<Integer> whichCol;
        List<Integer[]> resData;
        List<String> resCol;
        
                
        public Query(InputReader in) {
            this.resCol = new ArrayList<>();
            this.resData = new ArrayList<>();
            this.type = 0;
            this.table1 = 0;
            this.table2 = 0;
            this.Parts =  new ArrayList[4];
            this.whichCol = new ArrayList<>();
            this.whichTable = new ArrayList<>();
            for(int i = 0; i < 4; i++)
            {
                tokenizer = new StringTokenizer(in.nextLine());
                Parts[i] = new ArrayList<>();
                while(tokenizer.hasMoreTokens())
                {
                    Parts[i].add(tokenizer.nextToken());
                }
            }
            in.nextLine();
        }
        
        int checkType() {
            if(Parts[0].get(1).equals("*") && Parts[1].size() == 2)
                return 1;
            if(Parts[0].get(1).equals("*") && Parts[1].size() == 3)
                return 2;
            return 3;
        }
        
        void storeColNameOfJoinTable(Table[] tables) {
            if(type == 1 || type == 2)
            {
                for(int i = 0; i < tables[table1].nC; i++)
                {
                    whichTable.add(table1);
                    whichCol.add(i);
                    resCol.add(tables[table1].cName[i]);
                }
                for(int i = 0; i < tables[table2].nC; i++)
                {
                    whichTable.add(table2);
                    whichCol.add(i);
                    resCol.add(tables[table2].cName[i]);
                }    
            }
            else
            {
                for(int i = 1, j = Parts[0].size(); i < j; i++)
                {
                    String tmp = Parts[0].get(i), tbl = "", col = "";
                    int k, l = tmp.length();
                    for(k = 0; k < l; k++)
                    {
                        if(tmp.charAt(k) == '.')
                            break;
                        tbl += tmp.charAt(k);
                    }
                    col = i < j - 1 ? tmp.substring(k + 1, tmp.length() - 1) : tmp.substring(k + 1);
                    
                    if(tbl.equals(tShortName1))
                    {
                        whichTable.add(table1);
                        whichCol.add(tables[table1].colId.get(col));
                    }
                    else
                    {
                        whichTable.add(table2);
                        whichCol.add(tables[table2].colId.get(col));
                    }
                    resCol.add(col);
                }
            }
        }
        
        void printRes() {
            int nRow = resData.size();
            int nCol = resCol.size();
            for(int i = 0; i < nCol; i++)
            {
                print(resCol.get(i) + (i < nCol - 1 ? " " : "\n"));
            }
            for(int i = 0; i < nRow; i++)
            {
                for(int j = 0; j < nCol; j++)
                {
                    print(resData.get(i)[j] + (j < nCol - 1 ? " " : "\n"));
                }
            }
            println("");
        }
        
        void exec(Table[] tables, Map<String, Integer> tableId) {
            tShortName1 = tName1 = Parts[1].get(1);
            tShortName2 = tName2 = Parts[2].get(1);
    
            table1 = tableId.get(tName1);
            table2 = tableId.get(tName2);
            
            this.type = checkType();
            
            if(type == 2 || type == 3)
            {
                tShortName1 = Parts[1].get(2);
                tShortName2 = Parts[2].get(2);
            }
            
            colName1 = Parts[3].get(1).substring(tShortName1.length() + 1);
            colName2 = Parts[3].get(3).substring(tShortName2.length() + 1);
            predicateCol1 = tables[table1].colId.get(colName1);
            predicateCol2 = tables[table2].colId.get(colName2);
            
            storeColNameOfJoinTable(tables);
            for(int i = 0; i < tables[table1].nD; i++)
            {
                for(int j = 0; j < tables[table2].nD; j++)
                {
                    if(tables[table1].data[i][predicateCol1] == 
                            tables[table2].data[j][predicateCol2])
                    {
                        int nCol = whichTable.size();
                        Integer[] tmpRow = new Integer[nCol];
                        for(int k = 0; k < nCol; k++)
                        {
                            int tId = whichTable.get(k), cId = whichCol.get(k);
                            int rId = table1 == tId ? i : j;
                            tmpRow[k] = tables[tId].data[rId][cId];
                        }
                        
                        resData.add(tmpRow);
                    }
                }
            }
            
            Collections.sort(resData, new Comparator<Integer[]>() {
                int cmp = 0;
                @Override
                public int compare(Integer[] v1, Integer[] v2) {
                    for(int i = 0, j = v1.length; i < j; i++)
                    {
                        cmp = v1[i].compareTo(v2[i]);
                        if(cmp != 0)
                            return cmp;
                        
                    }
                    return 1;
                }
            });
            
            printRes();
        }
    }
}
