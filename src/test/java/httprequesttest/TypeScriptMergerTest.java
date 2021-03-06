package httprequesttest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertEquals;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;

import org.apache.commons.io.IOUtils;
import org.junit.Test;

import com.devonfw.cobigen.api.exception.MergeException;

import externalprocess.ExternalProcessHandler;
import externalprocess.ProcessConstants;
import requests.NodeMerger;

/**
 * Test methods for different TS mergers of the plugin
 */
public class TypeScriptMergerTest {

  /** Test resources root path */
  private static String testFileRootPath = "src/test/resources/testdata/unittest/merger/";

  /**
   * Checks if the ts-merger can be launched and if the iutput is correct with patchOverrides = false
   *
   * @test fails
   */
  @Test
  public void testMergingNoOverrides() {

    ExternalProcessHandler request = ExternalProcessHandler.getExternalProcessHandler(ProcessConstants.hostName,
        ProcessConstants.port);

    assertEquals(request.ExecutinExe(ProcessConstants.exePath), true);
    assertEquals(request.InitializeConnection(), true);

    // arrange
    File baseFile = new File(testFileRootPath + "baseFile.ts");
    File patchFile = new File(testFileRootPath + "baseFile.ts");
    // Next version should merge comments
    // String regex = " * Should format correctly this line";

    // act
    String mergedContents = new NodeMerger("tsmerge", false).merge(baseFile, patchFile.getAbsolutePath(), "UTF-8");

    assertThat(mergedContents).contains("bProperty");
    assertThat(mergedContents).contains("aProperty: number = 2");
    assertThat(mergedContents).contains("bMethod");
    assertThat(mergedContents).contains("aMethod");
    assertThat(mergedContents).contains("bProperty");
    assertThat(mergedContents).contains("import { c, f } from 'd'");
    assertThat(mergedContents).contains("import { a, e } from 'b'");
    assertThat(mergedContents).contains("export { e, g } from 'f';");
    assertThat(mergedContents).contains("export interface a {");
    assertThat(mergedContents).contains("private b: number;");
    // assertThat(mergedContents).containsPattern(regex);

    mergedContents = new NodeMerger("tsmerge", false).merge(baseFile, readTSFile("patchFile.ts"), "ISO-8859-1");

    assertThat(mergedContents).contains("bProperty");
    assertThat(mergedContents).contains("aProperty: number = 2");
    assertThat(mergedContents).contains("bMethod");
    assertThat(mergedContents).contains("aMethod");
    assertThat(mergedContents).contains("bProperty");
    assertThat(mergedContents).contains("import { c, f } from 'd'");
    assertThat(mergedContents).contains("import { a, e } from 'b'");
    assertThat(mergedContents).contains("export { e, g } from 'f';");
    assertThat(mergedContents).contains("export interface a {");
    assertThat(mergedContents).contains("private b: number;");
    // assertThat(mergedContents).containsPattern(regex);

    request.closeConnection();
  }

  /**
   * Checks if the ts-merger can be launched and if the iutput is correct with patchOverrides = true
   *
   * @test fails
   */
  @Test
  public void testMergingOverrides() {

    ExternalProcessHandler request = ExternalProcessHandler.getExternalProcessHandler(ProcessConstants.hostName,
        ProcessConstants.port);

    assertEquals(request.ExecutinExe(ProcessConstants.exePath), true);
    assertEquals(request.InitializeConnection(), true);

    // arrange
    File baseFile = new File(testFileRootPath + "baseFile.ts");

    // act
    String mergedContents = new NodeMerger("tsmerge", true).merge(baseFile, readTSFile("patchFile.ts"), "UTF-8");

    assertThat(mergedContents).contains("bProperty");
    assertThat(mergedContents).contains("aProperty: number = 3");
    assertThat(mergedContents).contains("bMethod");
    assertThat(mergedContents).contains("aMethod");
    assertThat(mergedContents).contains("bProperty");
    assertThat(mergedContents).contains("import { c, f } from 'd'");
    assertThat(mergedContents).contains("import { a, e } from 'b'");
    assertThat(mergedContents).contains("export { e, g } from 'f';");
    assertThat(mergedContents).contains("interface a {");
    assertThat(mergedContents).contains("private b: string;");
    // Next version should merge comments
    // assertThat(mergedContents).contains("// Should contain this comment");

    mergedContents = new NodeMerger("tsmerge", true).merge(baseFile, readTSFile("patchFile.ts"), "ISO-8859-1");

    assertThat(mergedContents).contains("bProperty");
    assertThat(mergedContents).contains("aProperty: number = 3");
    assertThat(mergedContents).contains("bMethod");
    assertThat(mergedContents).contains("aMethod");
    assertThat(mergedContents).contains("bProperty");
    assertThat(mergedContents).contains("import { c, f } from 'd'");
    assertThat(mergedContents).contains("import { a, e } from 'b'");
    assertThat(mergedContents).contains("export { e, g } from 'f';");
    assertThat(mergedContents).contains("interface a {");
    assertThat(mergedContents).contains("private b: string;");
    // Next version should merge comments
    // assertThat(mergedContents).contains("// Should contain this comment");
    request.closeConnection();
  }

  /**
   * Tests whether the contents will be rewritten after parsing and printing with the right encoding
   *
   * @throws IOException test fails
   * @test fails
   */
  @Test
  public void testReadingEncoding() throws IOException {

    File baseFile = new File(testFileRootPath + "baseFile_encoding_UTF-8.ts");
    File patchFile = new File(testFileRootPath + "patchFile.ts");
    String mergedContents = "";
    assertThat(mergedContents.contains("Ñ")).isTrue();

    baseFile = new File(testFileRootPath + "baseFile_encoding_ISO-8859-1.ts");
    mergedContents = "";
    assertThat(mergedContents.contains("Ñ"));
  }

  /**
   * Reads a TS file
   *
   * @param fileName the ts file
   * @return the content of the file
   */
  private String readTSFile(String fileName) {

    File patchFile = new File(testFileRootPath + fileName);
    String file = patchFile.getAbsolutePath();
    Reader reader = null;
    String returnString;

    try {
      reader = new FileReader(file);
      returnString = IOUtils.toString(reader);
      reader.close();

    } catch (FileNotFoundException e) {
      throw new MergeException(patchFile, "Can not read file " + patchFile.getAbsolutePath());
    } catch (IOException e) {
      throw new MergeException(patchFile, "Can not read the base file " + patchFile.getAbsolutePath());
    }

    return returnString;
  }

}
