package com.bskyb.internettv.spike;

import com.bskyb.internettv.spike.DemoService.BusinessException;
import com.bskyb.internettv.spike.util.JerseyClientRule;
import org.junit.Rule;
import org.junit.Test;

import javax.ws.rs.*;

import static com.bskyb.internettv.spike.util.ExceptionTesting.expect;
import static javax.ws.rs.core.MediaType.APPLICATION_JSON;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.*;

public class DemoServiceTest {

    private final MockDependency mockDependency = mock(MockDependency.class);

    @Rule
    public final JerseyClientRule jerseyClientRule = new JerseyClientRule(mockDependency);

    private final DemoService testObj = new DemoService(jerseyClientRule.target());


    @Test
    public void loadSomeData_shouldReturnTheRightDataFromTheDependency() throws Exception {
        //given
        String param1 = "meh";
        String param2 = "blah";
        String expectedResult = "some data";

        when(mockDependency.someResource(anyString(), anyString())).thenReturn(expectedResult);

        //when
        String result = testObj.loadSomeData(param1, param2);

        //then
        assertThat(result).isEqualTo(expectedResult);
        verify(mockDependency).someResource(param1, param2);
    }

    @Test
    public void loadSomeData_shouldThrowABusinessExceptionIfDependencyFails() throws Exception {
        //given
        String param1 = "meh";
        String param2 = "blah";
        when(mockDependency.someResource(anyString(), anyString())).thenThrow(new InternalServerErrorException());

        //when
        BusinessException exception = expect(BusinessException.class, () -> testObj.loadSomeData(param1, param2));

        //then
        assertThat(exception).hasMessage("Boom: 500");
        verify(mockDependency).someResource(param1, param2);
    }

    @Path("path/to")
    public interface MockDependency {

        @GET
        @Path("what/i/want/{pathParam}")
        @Produces(APPLICATION_JSON)
        String someResource(@PathParam("pathParam") String pathParam, @QueryParam("queryParam") String queryParam);
    }
}