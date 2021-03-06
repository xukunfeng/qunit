package com.qunar.base.qunit;

import com.qunar.base.qunit.casefilter.CaseFilter;
import com.qunar.base.qunit.casefilter.CaseIDsFilter;
import com.qunar.base.qunit.casefilter.TagFilter;
import com.qunar.base.qunit.reporter.QJSONReporter;
import com.qunar.base.qunit.reporter.Reporter;
import org.apache.commons.lang.StringUtils;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

public class QunitOptions {
    private Qunit.Options options;

    private Class<?> testClass;

    public QunitOptions(Class<?> testClass) {
        this.testClass = testClass;
        options = testClass.getAnnotation(Qunit.Options.class);
    }

    public List<String> tags() {
        String tags = System.getProperty("tags");
        if (StringUtils.isNotBlank(tags)) {
            return Arrays.asList(StringUtils.split(tags, ","));
        }
        return Arrays.asList(this.options.tags());
    }

    public List<String> levels(){
        String levels = System.getProperty("levels");
        if (StringUtils.isNotBlank(levels)){
            return Arrays.asList(StringUtils.split(levels, ","));
        }
        return Arrays.asList(this.options.levels());
    }

    public List<String> statuss(){
        String statuss = System.getProperty("statuss");
        if (StringUtils.isNotBlank(statuss)){
            return Arrays.asList(StringUtils.split(statuss, ","));
        }
        return Arrays.asList(this.options.statuss());
    }

    public String ids() {
        String ids = System.getProperty("ids");
        if (StringUtils.isNotBlank(ids)) {
            return ids;
        }
        return this.options.ids();
    }

    public String global() {
        return this.options.global();
    }

    public List<String> testCases() {
        return getTestFiles(this.options.files());
    }

    public List<String> dataCases() {
        return getTestFiles(this.options.dataFiles());
    }

    public List<String> before() {
        return getTestFiles(this.options.before());
    }

    public List<String> after() {
        return getTestFiles(this.options.after());
    }

    private List<String> getTestFiles(String[] files) {
        List<String> testDirs = Arrays.asList(files);
        TestCaseFileFinder testCaseFileFinder = new TestCaseFileFinder();
        return testCaseFileFinder.getTestCaseList(testDirs);
    }

    public Reporter reporter() {
        return new QJSONReporter(getOutput());
    }

    private Appendable getOutput() {
        String fileName = generateFileName();
        try {
            File output = new File(fileName);
            if (!output.exists()) {
                output.createNewFile();
            }
            return new FileWriter(output);
        } catch (IOException e) {
            return System.out;
        }
    }

    private String generateFileName() {
        return String.format("target/qunit-%s.json", this.testClass.getName());
    }

    public List<String> serviceConfig() {
        return getTestFiles(this.options.service());
        //return this.options.service();
    }

    public String keyFile(){
        return this.options.keyFile();
    }

    public List<String> dslFile() {
        return getTestFiles(this.options.dsl());
    }

    public CaseFilter createCaseFilter(Set<String> extraTagSet) {
        if (StringUtils.isBlank(ids())) {
            return new TagFilter(mergeTag(tags(), extraTagSet));
        } else {
            return new CaseIDsFilter(ids());
        }
    }

    private List<String> mergeTag(List<String> tags, Set<String> extraTags) {
        if (extraTags == null) {
            return tags;
        }
        List<String> mergeTags = new ArrayList<String>(tags);
        for (String extraTag : extraTags) {
            if (!tags.contains(extraTag)) {
                mergeTags.add(extraTag);
            }
        }
        return mergeTags;
    }

    public String jobName() {
        return System.getProperty("job");
    }

    public String buildNumber() {
        return System.getProperty("build");
    }
}
