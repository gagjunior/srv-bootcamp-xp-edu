package br.com.gagjunior.bootcampxpedu.controller.api.v1;

import br.com.gagjunior.bootcampxpedu.dto.ProdutoRequest;
import br.com.gagjunior.bootcampxpedu.dto.ProdutoResponse;
import br.com.gagjunior.bootcampxpedu.model.Produtos;
import br.com.gagjunior.bootcampxpedu.service.ProdutosService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("Testes unitários para ProdutosController")
class ProdutosControllerTest {

    @Mock
    private ProdutosService produtosService;

    private ProdutosController controller;

    @BeforeEach
    void setUp() {
        controller = new ProdutosController(produtosService);
    }

    @AfterEach
    void tearDown() {
        RequestContextHolder.resetRequestAttributes();
    }

    @Test
    void shouldFindAllProductsWithoutCodeFilter() {
        Produtos first = product("1", "SKU-1");
        Produtos second = product("2", "SKU-2");
        when(produtosService.findAll()).thenReturn(List.of(first, second));

        ResponseEntity<List<ProdutoResponse>> response = controller.findAll(null);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).containsExactly(ProdutoResponse.from(first), ProdutoResponse.from(second));
        verify(produtosService).findAll();
    }

    @ParameterizedTest
    @ValueSource(strings = {"SKU-1", "SKU-2"})
    void shouldUseCodeFilterWhenCodeIsProvided(String code) {
        Produtos expected = product("1", code);
        when(produtosService.findByCodigo(code)).thenReturn(expected);

        assertThat(controller.findAll(code).getBody()).containsExactly(ProdutoResponse.from(expected));
        verify(produtosService).findByCodigo(code);
    }

    @Test
    void shouldFindProductById() {
        Produtos expected = product("1", "SKU-1");
        when(produtosService.findById("1")).thenReturn(expected);

        ResponseEntity<ProdutoResponse> response = controller.findById("1");

        assertThat(response.getBody()).isEqualTo(ProdutoResponse.from(expected));
        verify(produtosService).findById("1");
    }

    @Test
    void shouldFindProductByCode() {
        Produtos expected = product("1", "SKU-1");
        when(produtosService.findByCodigo(expected.codigo())).thenReturn(expected);

        ResponseEntity<ProdutoResponse> response = controller.findByCodigo(expected.codigo());

        assertThat(response.getBody()).isEqualTo(ProdutoResponse.from(expected));
        verify(produtosService).findByCodigo(expected.codigo());
    }

    @Test
    void shouldCreateProductAndReturnLocation() {
        MockHttpServletRequest request = request("/api/v1/produtos");
        RequestContextHolder.setRequestAttributes(new ServletRequestAttributes(request));
        ProdutoRequest produtoRequest = new ProdutoRequest(" SKU-1 ", "Teclado", "Mecânico", 249.90);
        Produtos saved = new Produtos("product-1", "SKU-1", "Teclado", "Mecânico", 249.90);
        when(produtosService.save(produtoRequest.toModel())).thenReturn(saved);

        ResponseEntity<ProdutoResponse> response = controller.create(produtoRequest);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(response.getHeaders().getLocation()).hasToString("http://localhost:8080/api/v1/produtos/product-1");
        assertThat(response.getBody()).isEqualTo(ProdutoResponse.from(saved));
        verify(produtosService).save(produtoRequest.toModel());
    }

    @Test
    void shouldUpdateProduct() {
        ProdutoRequest request = new ProdutoRequest("SKU-2", "Mouse", "Sem fio", 99.90);
        Produtos updated = new Produtos("1", "SKU-2", "Mouse", "Sem fio", 99.90);
        when(produtosService.update("1", request.toModel())).thenReturn(updated);

        ResponseEntity<ProdutoResponse> response = controller.update("1", request);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isEqualTo(ProdutoResponse.from(updated));
        verify(produtosService).update("1", request.toModel());
    }

    @Test
    void shouldDeleteProduct() {
        ResponseEntity<Void> response = controller.delete("1");

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
        assertThat(response.getBody()).isNull();
        verify(produtosService).deleteById("1");
    }

    private static Produtos product(String id, String code) {
        return new Produtos(id, code, "Product", "Description", 10.0);
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
