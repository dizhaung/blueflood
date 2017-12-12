/*
 * Copyright 2013 Rackspace
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */

package com.rackspacecloud.blueflood.io;

import org.elasticsearch.common.xcontent.XContentBuilder;
import org.junit.Assert;
import org.junit.Test;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class DiscoveryTest {

    final String TENANT_1 = "987654";
    final String METRIC_NAME_A = "metric.a.b.c.d.1";

    @Test
    public void Discovery_BothParametersValid_CreateInstance() {
        Discovery discovery = new Discovery(TENANT_1, METRIC_NAME_A);
        Assert.assertNotNull("Discovery object is null.", discovery);
        Assert.assertEquals("metricName did not match.", METRIC_NAME_A, discovery.getMetricName());
        Assert.assertEquals("tenantId did not match.", TENANT_1, discovery.getTenantId());
        Assert.assertNotNull(discovery.getFields());

        //Assert.assertEquals(TENANT_1 + ":" + METRIC_NAME_A, discovery.getDocumentId());
        //Assert.assertNotNull(discovery.toString());
    }

    @Test(expected = IllegalArgumentException.class)
    public void Discovery_NullTenantId_IllegalArgumentExceptionThrown(){
        Discovery discovery = new Discovery(null, METRIC_NAME_A);
    }

    @Test(expected = IllegalArgumentException.class)
    public void Discovery_EmptyTenantId_IllegalArgumentExceptionThrown(){
        Discovery discovery = new Discovery("", METRIC_NAME_A);
    }

    @Test(expected = IllegalArgumentException.class)
    public void Discovery_WhiteSpacedTenantId_IllegalArgumentExceptionThrown(){
        Discovery discovery = new Discovery("   ", METRIC_NAME_A);
    }

    @Test(expected = IllegalArgumentException.class)
    public void Discovery_NullMetricName_IllegalArgumentExceptionThrown(){
        Discovery discovery = new Discovery(TENANT_1, null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void Discovery_EmptyMetricName_IllegalArgumentExceptionThrown(){
        Discovery discovery = new Discovery(TENANT_1,"");
    }

    @Test(expected = IllegalArgumentException.class)
    public void Discovery_WhiteSpacedMetricName_IllegalArgumentExceptionThrown(){
        Discovery discovery = new Discovery(TENANT_1,"   ");
    }

    @Test
    public void getDocumentId_returns_validDocumentId(){
        Discovery discovery = new Discovery(TENANT_1, METRIC_NAME_A);
        Assert.assertEquals(TENANT_1 + ":" + METRIC_NAME_A, discovery.getDocumentId());
    }

    @Test
    public void toString_returnsExpectedString(){
        Map<String, Object> fields = new HashMap<>();
        fields.put("a_1", "a1");
        fields.put("a_2", "a2");
        Discovery discovery = new Discovery(TENANT_1, METRIC_NAME_A).withSourceFields(fields);

        String actualValue = discovery.toString();
        Assert.assertNotNull(actualValue);

        String expectedString = "ElasticMetricDiscovery " +
                "[tenantId=" + TENANT_1 +
                ", metricName=" + METRIC_NAME_A +
                ", fields={a_1=a1, a_2=a2}]";
        Assert.assertEquals(expectedString, actualValue);
    }


    @Test
    public void getFields_returnsExpectedKeyValuePairs() {
        Map<String, Object> fields = new HashMap<>();
        fields.put("a_1", "a1");
        fields.put("a_2", "a2");
        fields.put("a_3", "a3");

        Discovery discovery = new Discovery(TENANT_1, METRIC_NAME_A).withSourceFields(fields);

        Map<String, Object> actualFields = discovery.getFields();
        Assert.assertEquals("a1", actualFields.get("a_1").toString());
        Assert.assertEquals("a2", actualFields.get("a_2").toString());
        Assert.assertEquals("a3", actualFields.get("a_3").toString());
    }

    @Test
    public void testCreateSourceContent() throws IOException {

        Map<String, Object> fields = new HashMap<String, Object>();
        fields.put(ESFieldLabel.unit.toString(), "quantum");

        Discovery discovery = new Discovery(TENANT_1, METRIC_NAME_A).withSourceFields(fields);
        XContentBuilder builder = discovery.createSourceContent();

        final String expectedString =
                "{" +
                    "\"tenantId\":\"" + TENANT_1 + "\"," +
                    "\"metric_name\":\"" + METRIC_NAME_A + "\"," +
                    "\"unit\":\"quantum\"" +
                "}";

        Assert.assertEquals(expectedString, builder.string());
    }
}
