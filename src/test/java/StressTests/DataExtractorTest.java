package StressTests;

import com.compomics.natter_remake.controllers.DataExtractor;
import com.compomics.natter_remake.controllers.DbConnectionController;
import com.compomics.natter_remake.controllers.DbDAO;
import com.compomics.natter_remake.model.Project;
import java.io.File;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author Davy
 */
public class DataExtractorTest {
    static List<Project> allProjects = new ArrayList<Project>();

    @BeforeClass
    public static void setUpClass() throws SQLException {
        DbConnectionController.createConnection("Davy", "aerodynamic", "muppet03", 3306, "projects");
        allProjects = DbDAO.getAllProjects();
    }

    @AfterClass
    public static void tearDownClass() {
    }

    @Before
    public void setUp() {
    }

    @After
    public void tearDown() {
    }

    /**
     * Test of extractDataInMem method, of class DataExtractor.
     */
    @Test
    public void testExtractDataInMem() throws Exception {
        System.out.println("extractDataInMem");
        Project project = null;
        List expResult = null;
        List result = DataExtractor.extractDataInMem(project);
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of extractDataLowMem method, of class DataExtractor.
     */
    @Test
    public void testExtractDataLowMem() throws Exception {
        System.out.println("extractDataLowMem");
        for (Project project : allProjects) {
            System.out.println(project.getProjectId());
            List result = DataExtractor.extractDataLowMem(project);
        }
    }

    /**
     * Test of extractDataToLocal method, of class DataExtractor.
     */
    @Test
    public void testExtractDataToLocal_Project() throws Exception {
        System.out.println("extractDataToLocal");
        Project project = null;
        DataExtractor.extractDataToLocal(project);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of extractDataToLocal method, of class DataExtractor.
     */
    @Test
    public void testExtractDataToLocal_Project_File() throws Exception {
        System.out.println("extractDataToLocal");
        Project project = null;
        File rovFileOutputLocationFolder = null;
        DataExtractor.extractDataToLocal(project, rovFileOutputLocationFolder);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of extractDataToLocalLowMem method, of class DataExtractor.
     */
    @Test
    public void testExtractDataToLocalLowMem() throws Exception {
        System.out.println("extractDataToLocalLowMem");
        File rovFileOutputLocationFolder = null;
        Project project = null;
        DataExtractor.extractDataToLocalLowMem(rovFileOutputLocationFolder, project);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }
}