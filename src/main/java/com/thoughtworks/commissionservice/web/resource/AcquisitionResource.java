package com.thoughtworks.commissionservice.web.resource;

import com.thoughtworks.commissionservice.service.AcquisitionService;
import com.thoughtworks.commissionservice.service.model.AcquisitionError;
import com.thoughtworks.commissionservice.web.dto.AcquisitionCommissionRequest;
import com.thoughtworks.commissionservice.web.dto.GeneralResponse;
import io.vavr.control.Either;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.Collections;

import static com.thoughtworks.commissionservice.service.ErrorMessage.*;

@RestController
@RequestMapping(path = "/commission-service/v1", produces = MediaType.APPLICATION_JSON_VALUE)
public class AcquisitionResource {

    @Autowired
    private AcquisitionService acquisitionService;

    @PostMapping(path = "/sales-persons/{salesPersonId}/commission-acquisitions")
    public ResponseEntity<GeneralResponse<Void>> acquisitionCommission(
            @PathVariable("salesPersonId") String salesPersonId,
            @RequestBody AcquisitionCommissionRequest request) {
        Either<AcquisitionError, String> result = acquisitionService.acquisitionCommission(salesPersonId, request.getOrderNo());
        if (result.isRight()) {
            String acquisitionId = result.get();
            return ResponseEntity
                    .created(URI.create(mergeURI("/sales-persons", salesPersonId, "commission-acquisitions", acquisitionId)))
                    .build();
        } else {
            GeneralResponse<Void> response = new GeneralResponse<>();
            switch (result.getLeft().getErrorMessage()) {
                case ORDER_NOT_RECEIVED:
                    response.setErrMessage(ORDER_NOT_RECEIVED.getMessage());
                    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
                case COMMISSION_ALREADY_ACQUIRED:
                    response.setErrMessage(COMMISSION_ALREADY_ACQUIRED.getMessage());
                    response.setLinks(
                            Collections.singletonMap("getAcquisitionInfo", mergeURI(
                                    "/sales-persons", salesPersonId, "commission-acquisitions", result.getLeft().getAcquisitionId()
                            )));
                    return ResponseEntity.status(HttpStatus.CONFLICT).body(response);
                case ORDER_NOT_FOUND:
                    response.setErrMessage(ORDER_NOT_FOUND.getMessage());
                    return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
                case RETRIEVE_ORDER_FAILED:
                    response.setErrMessage(RETRIEVE_ORDER_FAILED.getMessage());
                    return ResponseEntity.status(HttpStatus.BAD_GATEWAY).body(response);
                default:
                    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
            }
        }
    }

    private String mergeURI(String... parts) {
        return String.join("/", parts);
    }
}
