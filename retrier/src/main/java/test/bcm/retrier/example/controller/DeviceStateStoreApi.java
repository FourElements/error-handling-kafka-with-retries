package test.bcm.retrier.example.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import test.bcm.retrier.common.model.output.DeviceOutput;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import javax.validation.Valid;

@Api(value = "DeviceOutput", tags = "DeviceOutput StateStoreRest API")
public interface DeviceStateStoreApi {

    @ApiOperation(value = "Retrieves a DeviceOutput by ID from internal state store", nickname = "retrieveDevice",
        notes = "This operation retrieves a DeviceOutput entity", response = DeviceOutput.class,
        tags = {"device",})
    @ApiResponses(value = {
        @ApiResponse(code = 200, message = "Success", response = DeviceOutput.class),
        @ApiResponse(code = 400, message = "Bad Request", response = Error.class),
        @ApiResponse(code = 401, message = "Unauthorized", response = Error.class),
        @ApiResponse(code = 403, message = "Forbidden", response = Error.class),
        @ApiResponse(code = 404, message = "Not Found", response = Error.class),
        @ApiResponse(code = 405, message = "Method Not allowed", response = Error.class),
        @ApiResponse(code = 409, message = "Conflict", response = Error.class),
        @ApiResponse(code = 500, message = "Internal Server Error", response = Error.class)})
    @GetMapping(value = "/device/{id}",
        produces = {"application/json;charset=utf-8"})
    ResponseEntity<DeviceOutput> retrieveDevice(@ApiParam(value = "Identifier of the Device", required = true) @PathVariable("id") String id,
        @ApiParam(value = "When sub-classing, this defines the sub-class entity name") @Valid @RequestParam(value = "type") String type,
        @ApiParam(value = "Comma-separated properties to provide in response") @Valid @RequestParam(value = "fields", required = false)
            String fields);
}
