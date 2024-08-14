package org.example;

import com.ibm.as400.access.*;
import com.ibm.as400.access.list.OpenListException;
import com.ibm.as400.access.list.SpooledFileListItem;
import com.ibm.as400.access.list.SpooledFileOpenList;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

/**
 * Ищет в списке spooled файлов последний для указанного файла
 */
public class As400SpooledFileFinder {
    public String fetchFileContent(AS400 as400Conn, CommandCall as400Cmd, String fileName) {
        try {
            SpooledFileOpenList list = new SpooledFileOpenList(as400Conn);

            list.setFilterUsers(new String[] { "*CURRENT" } );
            list.addSortField(SpooledFileOpenList.JOB_NUMBER, false);
            list.open();
            var items = list.getItems();

            while (items.hasMoreElements()) {
                SpooledFileListItem item = (SpooledFileListItem)items.nextElement();

                if (!item.getName().equals(fileName)) {
                    continue;
                }

                SpooledFile spooledFile = new SpooledFile(
                    as400Conn, // AS400
                    item.getName(), // splf name
                    item.getNumber(), // splf number
                    item.getJobName(), // job name
                    item.getJobUser(), // job user
                    item.getJobNumber() ); // job number

                PrintParameterList printParms = new PrintParameterList();
                printParms.setParameter(PrintObject.ATTR_WORKSTATION_CUST_OBJECT,  "/QSYS.LIB/QWPDEFAULT.WSCST");
                printParms.setParameter(PrintObject.ATTR_MFGTYPE, "*WSCST");
                PrintObjectTransformedInputStream is = spooledFile.getTransformedInputStream(printParms);

                BufferedReader d = new BufferedReader(new InputStreamReader(is));
                String data = "";
                String result = "";
                while((data = d.readLine()) != null) {
                    result += "\n" + data;
                }

                return result;
            }
            list.close();
        } catch (AS400SecurityException e) {
            throw new RuntimeException(e);
        } catch (ErrorCompletingRequestException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        } catch (RequestNotSupportedException e) {
            throw new RuntimeException(e);
        } catch (OpenListException e) {
            throw new RuntimeException(e);
        } catch (ObjectDoesNotExistException e) {
            throw new RuntimeException(e);
        }

        return "";
    }
}
