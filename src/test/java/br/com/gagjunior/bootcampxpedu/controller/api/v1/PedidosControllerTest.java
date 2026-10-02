package br.com.gagjunior.bootcampxpedu.controller.api.v1;

import br.com.gagjunior.bootcampxpedu.dto.PedidoRequest;
import br.com.gagjunior.bootcampxpedu.dto.PedidoResponse;
import br.com.gagjunior.bootcampxpedu.model.Pedidos;
import br.com.gagjunior.bootcampxpedu.service.PedidosService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatIllegalArgumentException;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("Testes unitários para PedidosController")
class PedidosControllerTest {

    @Mock
    private PedidosService pedidosService;

    private PedidosController controller;

    @BeforeEach
    void setUp() {
        controller = new PedidosController(pedidosService);
    }

    @AfterEach
    void tearDown() {
        RequestContextHolder.resetRequestAttributes();
    }

    @Test
    void shouldFindAllOrdersWithoutFilters() {
        Pedidos first = order("1", "client-1", "NEW");
        Pedidos second = order("2", "client-2", "PAID");
        when(pedidosService.findAll()).thenReturn(List.of(first, second));

        ResponseEntity<List<PedidoResponse>> response = controller.findAll(null, null);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).containsExactly(PedidoResponse.from(first), PedidoResponse.from(second));
        verify(pedidosService).findAll();
    }

    @Test
    void shouldFindOrdersByClientWhenClientFilterIsProvided() {
        List<Pedidos> expected = List.of(order("1", "client-1", "NEW"));
        when(pedidosService.findByClienteId("client-1")).thenReturn(expected);

        ResponseEntity<List<PedidoResponse>> response = controller.findAll("client-1", null);

        assertThat(response.getBody()).containsExactly(PedidoResponse.from(expected.getFirst()));
        verify(pedidosService).findByClienteId("client-1");
    }

    @Test
    void shouldFindOrdersByStatusWhenStatusFilterIsProvided() {
        List<Pedidos> expected = List.of(order("1", "client-1", "PAID"));
        when(pedidosService.findByStatus("PAID")).thenReturn(expected);

        ResponseEntity<List<PedidoResponse>> response = controller.findAll(null, "PAID");

        assertThat(response.getBody()).containsExactly(PedidoResponse.from(expected.getFirst()));
        verify(pedidosService).findByStatus("PAID");
    }

    @ParameterizedTest
    @CsvSource({
            "client-1, NEW",
            "client-2, PAID"
    })
    void shouldRejectUsingBothOrderFilters(String clientId, String status) {
        assertThatIllegalArgumentException()
                .isThrownBy(() -> controller.findAll(clientId, status))
                .withMessage("informe apenas um dos filtros: clienteId ou status");
        verifyNoInteractions(pedidosService);
    }

    @Test
    void shouldFindOrderById() {
        Pedidos expected = order("1", "client-1", "NEW");
        when(pedidosService.findById("1")).thenReturn(expected);

        ResponseEntity<PedidoResponse> response = controller.findById("1");

        assertThat(response.getBody()).isEqualTo(PedidoResponse.from(expected));
        verify(pedidosService).findById("1");
    }

    @Test
    void shouldFindOrdersByClientId() {
        List<Pedidos> expected = List.of(order("1", "client-1", "NEW"));
        when(pedidosService.findByClienteId("client-1")).thenReturn(expected);

        ResponseEntity<List<PedidoResponse>> response = controller.findByClienteId("client-1");

        assertThat(response.getBody()).containsExactly(PedidoResponse.from(expected.getFirst()));
        verify(pedidosService).findByClienteId("client-1");
    }

    @Test
    void shouldFindOrdersByStatus() {
        List<Pedidos> expected = List.of(order("1", "client-1", "PAID"));
        when(pedidosService.findByStatus("PAID")).thenReturn(expected);

        ResponseEntity<List<PedidoResponse>> response = controller.findByStatus("PAID");

        assertThat(response.getBody()).containsExactly(PedidoResponse.from(expected.getFirst()));
        verify(pedidosService).findByStatus("PAID");
    }

    @Test
    void shouldCreateOrderAndReturnLocation() {
        MockHttpServletRequest request = request();
        RequestContextHolder.setRequestAttributes(new ServletRequestAttributes(request));
        PedidoRequest pedidoRequest = new PedidoRequest("client-1", List.of("product-1"), 149.90, "NEW");
        Pedidos saved = order("order-1", "client-1", "NEW");
        when(pedidosService.save(pedidoRequest.toModel())).thenReturn(saved);

        ResponseEntity<PedidoResponse> response = controller.create(pedidoRequest);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(response.getHeaders().getLocation()).hasToString("http://localhost:8080/api/v1/pedidos/order-1");
        assertThat(response.getBody()).isEqualTo(PedidoResponse.from(saved));
        verify(pedidosService).save(pedidoRequest.toModel());
    }

    @Test
    void shouldUpdateOrder() {
        PedidoRequest request = new PedidoRequest("client-2", List.of("product-2"), 249.90, "PAID");
        Pedidos updated = new Pedidos("1", "client-2", List.of("product-2"), 249.90, "PAID");
        when(pedidosService.update("1", request.toModel())).thenReturn(updated);

        ResponseEntity<PedidoResponse> response = controller.update("1", request);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isEqualTo(PedidoResponse.from(updated));
        verify(pedidosService).update("1", request.toModel());
    }

    @Test
    void shouldDeleteOrder() {
        ResponseEntity<Void> response = controller.delete("1");

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
        assertThat(response.getBody()).isNull();
        verify(pedidosService).deleteById("1");
    }

    private static Pedidos order(String id, String clientId, String status) {
        return new Pedidos(id, clientId, List.of("product-1"), 149.90, status);
    }

    private static MockHttpServletRequest request() {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setScheme("http");
        request.setServerName("localhost");
        request.setServerPort(8080);
        request.setRequestURI("/api/v1/pedidos");
        return request;
    }
}
