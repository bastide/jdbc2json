/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.github.bastide.jdbc2json.hsqldb;

import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import org.junit.After;
import org.junit.AfterClass;
import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.hsqldb.cmdline.SqlFile;
import org.hsqldb.cmdline.SqlToolError;

/**
 *
 * @author rbastide
 */
public class HsqlDBTest {
    private static final String[] initStatements = {
    };
    private Connection connection;

    public HsqlDBTest() {
    }

    @BeforeClass
    public static void setUpClass() {
    }

    @AfterClass
    public static void tearDownClass() {
    }

    @Before
    public void setUp() throws SQLException, IOException, SqlToolError {
        connection = DriverManager.getConnection("jdbc:hsqldb:mem:testcase;shutdown=true", "SA", null);
        String sqlFilePath = getClass().getResource("/fillTestDatabase.sql").getFile();
        SqlFile sqlFile = new SqlFile(new File(sqlFilePath));

        sqlFile.setConnection(connection);
        sqlFile.execute();
        sqlFile.closeReader(); 
	
	Statement stmt = connection.createStatement();
        for (String sql : initStatements)
            stmt.execute(sql);
        
	connection.commit();
    }

    @After
    public void tearDown() throws SQLException {
        connection.close();
    }

    @Test
    public void hello() throws SQLException {
        Statement stmt = connection.createStatement();
        try (ResultSet rs = stmt.executeQuery("SELECT * FROM greeting")) {
            rs.next();
            String message = rs.getString("message");
            assertEquals("hello", message);
            rs.next();
            message = rs.getString("message");
            assertEquals("Iñtërnâtiônàlizætiøn", message);
        }
    }
}
