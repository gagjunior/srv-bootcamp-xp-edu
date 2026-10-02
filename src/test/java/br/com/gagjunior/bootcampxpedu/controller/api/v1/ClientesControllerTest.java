package br.com.gagjunior.bootcampxpedu.controller.api.v1;

import br.com.gagjunior.bootcampxpedu.dto.ClienteRequest;
import br.com.gagjunior.bootcampxpedu.dto.ClienteResponse;
import br.com.gagjunior.bootcampxpedu.model.Clientes;
import br.com.gagjunior.bootcampxpedu.service.ClientesService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.junit.jupiter.api.extension.ExtendWith;
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
@DisplayName("Testes unitários para ClientesController")
class ClientesControllerTest {

    @Mock
    private ClientesService clientesService;

    private ClientesController controller;

    @BeforeEach
    void setUp() {
        controller = new ClientesController(clientesService);
    }

    @AfterEach
    void tearDown() {
        RequestContextHolder.resetRequestAttributes();
    }

    @Test
    void shouldFindAllClientsWithoutFilters() {
        Clientes first = client("1", "Ana", "12345678900", "ana@example.com");
        Clientes second = client("2", "Bia", "98765432100", "bia@example.com");
        when(clientesService.findAll()).thenReturn(List.of(first, second));

        ResponseEntity<List<ClienteResponse>> response = controller.findAll(null, null);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).containsExactly(ClienteResponse.from(first), ClienteResponse.from(second));
        verify(clientesService).findAll();
    }

    @Test
    void shouldFindClientByCpfWhenCpfFilterIsProvided() {
        Clientes expected = client("1", "Ana", "12345678900", "ana@example.com");
        when(clientesService.findByCpf(expected.cpf())).thenReturn(expected);

        ResponseEntity<List<ClienteResponse>> response = controller.findAll(expected.cpf(), null);

        assertThat(response.getBody()).containsExactly(ClienteResponse.from(expected));
        verify(clientesService).findByCpf(expected.cpf());
    }

    @Test
    void shouldFindClientByEmailWhenEmailFilterIsProvided() {
        Clientes expected = client("1", "Ana", "12345678900", "ana@example.com");
        when(clientesService.findByEmail(expected.email())).thenReturn(expected);

        ResponseEntity<List<ClienteResponse>> response = controller.findAll(null, expected.email());

        assertThat(response.getBody()).containsExactly(ClienteResponse.from(expected));
        verify(clientesService).findByEmail(expected.email());
    }

    @ParameterizedTest
    @CsvSource({
            "12345678900, ana@example.com",
            "cpf-2, email-2"
    })
    void shouldRejectUsingBothClientFilters(String cpf, String email) {
        assertThatIllegalArgumentException()
                .isThrownBy(() -> controller.findAll(cpf, email))
                .withMessage("informe apenas um dos filtros: cpf ou email");
        verifyNoInteractions(clientesService);
    }

    @Test
    void shouldFindClientById() {
        Clientes expected = client("1", "Ana", "12345678900", "ana@example.com");
        when(clientesService.findById("1")).thenReturn(expected);

        ResponseEntity<ClienteResponse> response = controller.findById("1");

        assertThat(response.getBody()).isEqualTo(ClienteResponse.from(expected));
        verify(clientesService).findById("1");
    }

    @Test
    void shouldFindClientByCpf() {
        Clientes expected = client("1", "Ana", "12345678900", "ana@example.com");
        when(clientesService.findByCpf(expected.cpf())).thenReturn(expected);

        ResponseEntity<ClienteResponse> response = controller.findByCpf(expected.cpf());

        assertThat(response.getBody()).isEqualTo(ClienteResponse.from(expected));
        verify(clientesService).findByCpf(expected.cpf());
    }

    @Test
    void shouldFindClientByEmail() {
        Clientes expected = client("1", "Ana", "12345678900", "ana@example.com");
        when(clientesService.findByEmail(expected.email())).thenReturn(expected);

        ResponseEntity<ClienteResponse> response = controller.findByEmail(expected.email());

        assertThat(response.getBody()).isEqualTo(ClienteResponse.from(expected));
        verify(clientesService).findByEmail(expected.email());
    }

    @Test
    void shouldCreateClientAndReturnLocation() {
        MockHttpServletRequest request = request("/api/v1/clientes");
        RequestContextHolder.setRequestAttributes(new ServletRequestAttributes(request));
        ClienteRequest clienteRequest = new ClienteRequest(" Ana Silva ", "12345678900", "ana@example.com");
        Clientes saved = client("client-1", "Ana Silva", "12345678900", "ana@example.com");
        when(clientesService.save(new Clientes(null, "Ana Silva", "12345678900", "ana@example.com")))
                .thenReturn(saved);

        ResponseEntity<ClienteResponse> response = controller.create(clienteRequest);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(response.getHeaders().getLocation()).hasToString("http://localhost:8080/api/v1/clientes/client-1");
        assertThat(response.getBody()).isEqualTo(ClienteResponse.from(saved));
        verify(clientesService).save(new Clientes(null, "Ana Silva", "12345678900", "ana@example.com"));
    }

    @Test
    void shouldUpdateClient() {
        ClienteRequest request = new ClienteRequest("Ana Atualizada", "12345678900", "ana.updated@example.com");
        Clientes updated = client("1", "Ana Atualizada", "12345678900", "ana.updated@example.com");
        when(clientesService.update("1", request.toModel())).thenReturn(updated);

        ResponseEntity<ClienteResponse> response = controller.update("1", request);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isEqualTo(ClienteResponse.from(updated));
        verify(clientesService).update("1", request.toModel());
    }

    @Test
    void shouldDeleteClient() {
        ResponseEntity<Void> response = controller.delete("1");

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
        assertThat(response.getBody()).isNull();
        verify(clientesService).deleteById("1");
    }

    private static Clientes client(String id, String name, String cpf, String email) {
        return new Clientes(id, name, cpf, email);
    }

    private static MockHttpServletRequest request(String uri) {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setScheme("http");
        request.setServerName("localhost");
        request.setServerPort(8080);
        request.setRequestURI(uri);
        return request;
    }
}
