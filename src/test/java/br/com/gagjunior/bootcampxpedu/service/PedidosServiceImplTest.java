package br.com.gagjunior.bootcampxpedu.service;

import br.com.gagjunior.bootcampxpedu.exception.ResourceNotFoundException;
import br.com.gagjunior.bootcampxpedu.model.Pedidos;
import br.com.gagjunior.bootcampxpedu.repository.ClientesRepository;
import br.com.gagjunior.bootcampxpedu.repository.PedidosRepository;
import br.com.gagjunior.bootcampxpedu.repository.ProdutosRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Collections;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatIllegalArgumentException;
import static org.assertj.core.api.Assertions.assertThatNullPointerException;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Testes unitários para PedidosServiceImpl")
class PedidosServiceImplTest {

    @Mock
    private PedidosRepository pedidosRepository;

    @Mock
    private ClientesRepository clientesRepository;

    @Mock
    private ProdutosRepository produtosRepository;

    private PedidosServiceImpl service;

    @BeforeEach
    void setUp() {
        service = new PedidosServiceImpl(pedidosRepository, clientesRepository, produtosRepository);
    }

    @Test
    @DisplayName("não permite dependências nulas")
    void shouldRejectNullDependencies() {
        assertThatNullPointerException().isThrownBy(() ->
                new PedidosServiceImpl(null, clientesRepository, produtosRepository));
        assertThatNullPointerException().isThrownBy(() ->
                new PedidosServiceImpl(pedidosRepository, null, produtosRepository));
        assertThatNullPointerException().isThrownBy(() ->
                new PedidosServiceImpl(pedidosRepository, clientesRepository, null));
    }

    @Test
    void shouldFindAllOrders() {
        List<Pedidos> orders = List.of(order("1"));
        when(pedidosRepository.findAll()).thenReturn(orders);

        assertThat(service.findAll()).isEqualTo(orders);
    }

    @Test
    void shouldPropagateRepositoryFailureWhenFindingAllOrders() {
        RuntimeException failure = new RuntimeException("database unavailable");
        when(pedidosRepository.findAll()).thenThrow(failure);

        assertThatThrownBy(() -> service.findAll()).isSameAs(failure);
    }

    @Test
    void shouldFindOrderById() {
        Pedidos expected = order("1");
        when(pedidosRepository.findById("1")).thenReturn(Optional.of(expected));

        assertThat(service.findById("1")).isSameAs(expected);
    }

    @Test
    void shouldThrowWhenOrderDoesNotExist() {
        when(pedidosRepository.findById("missing")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.findById("missing"))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("Recurso Pedido não encontrado: missing");
    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {" "})
    void shouldRejectInvalidOrderId(String id) {
        assertThatThrownBy(() -> service.findById(id))
                .isInstanceOfAny(NullPointerException.class, IllegalArgumentException.class);
        verifyNoInteractions(pedidosRepository, clientesRepository, produtosRepository);
    }

    @Test
    void shouldFindOrdersByClient() {
        List<Pedidos> orders = List.of(order("1"));
        when(pedidosRepository.findByClienteId("client-1")).thenReturn(orders);

        assertThat(service.findByClienteId("client-1")).isEqualTo(orders);
    }

    @Test
    void shouldFindOrdersByStatus() {
        List<Pedidos> orders = List.of(order("1"));
        when(pedidosRepository.findByStatus("PAID")).thenReturn(orders);

        assertThat(service.findByStatus("PAID")).isEqualTo(orders);
    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {" "})
    void shouldRejectInvalidClientIdAndStatus(String value) {
        assertThatThrownBy(() -> service.findByClienteId(value))
                .isInstanceOfAny(NullPointerException.class, IllegalArgumentException.class);
        assertThatThrownBy(() -> service.findByStatus(value))
                .isInstanceOfAny(NullPointerException.class, IllegalArgumentException.class);
        verifyNoInteractions(pedidosRepository, clientesRepository, produtosRepository);
    }

    @Test
    void shouldSaveOrderWhenAllReferencesExist() {
        Pedidos newOrder = order(null);
        when(clientesRepository.existsById("client-1")).thenReturn(true);
        when(produtosRepository.existsById("product-1")).thenReturn(true);
        when(pedidosRepository.save(newOrder)).thenReturn(newOrder);

        assertThat(service.save(newOrder)).isSameAs(newOrder);
        verify(pedidosRepository).save(newOrder);
    }

    @Test
    void shouldRejectNullOrderOnSave() {
        assertThatNullPointerException().isThrownBy(() -> service.save(null))
                .withMessage("pedido não pode ser nulo");
        verifyNoInteractions(pedidosRepository, clientesRepository, produtosRepository);
    }

    @Test
    void shouldRejectOrderWhenClientDoesNotExist() {
        when(clientesRepository.existsById("client-1")).thenReturn(false);

        assertThatThrownBy(() -> service.save(order(null)))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("Recurso Cliente referenciado pelo pedido não encontrado: client-1");
        verifyNoInteractions(produtosRepository);
        verify(pedidosRepository, never()).save(any());
    }

    @Test
    void shouldRejectOrderWhenProductDoesNotExist() {
        when(clientesRepository.existsById("client-1")).thenReturn(true);
        when(produtosRepository.existsById("product-1")).thenReturn(false);

        assertThatThrownBy(() -> service.save(order(null)))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("Recurso Produto referenciado pelo pedido não encontrado: product-1");
        verify(pedidosRepository, never()).save(any());
    }

    @Test
    void shouldUpdateOrderAndKeepItsId() {
        Pedidos existing = order("1");
        Pedidos changes = new Pedidos(null, "client-2", List.of("product-2"), 25.0, "SHIPPED");
        Pedidos expected = new Pedidos("1", "client-2", List.of("product-2"), 25.0, "SHIPPED");
        when(pedidosRepository.findById("1")).thenReturn(Optional.of(existing));
        when(clientesRepository.existsById("client-2")).thenReturn(true);
        when(produtosRepository.existsById("product-2")).thenReturn(true);
        when(pedidosRepository.save(expected)).thenReturn(expected);

        assertThat(service.update("1", changes)).isEqualTo(expected);
        verify(pedidosRepository).save(expected);
    }

    @Test
    void shouldRejectUpdateOfMissingOrder() {
        when(pedidosRepository.findById("missing")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.update("missing", order(null)))
                .isInstanceOf(ResourceNotFoundException.class);
        verifyNoInteractions(clientesRepository, produtosRepository);
    }

    @Test
    void shouldRejectNullOrderOnUpdate() {
        when(pedidosRepository.findById("1")).thenReturn(Optional.of(order("1")));

        assertThatNullPointerException().isThrownBy(() -> service.update("1", null));
        verifyNoInteractions(clientesRepository, produtosRepository);
    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {" "})
    void shouldRejectInvalidIdOnUpdateAndDelete(String id) {
        assertThatThrownBy(() -> service.update(id, order(null)))
                .isInstanceOfAny(NullPointerException.class, IllegalArgumentException.class);
        assertThatThrownBy(() -> service.deleteById(id))
                .isInstanceOfAny(NullPointerException.class, IllegalArgumentException.class);
        verifyNoInteractions(pedidosRepository, clientesRepository, produtosRepository);
    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {" "})
    void shouldRejectInvalidClientIdStatusAndProductIdOnSave(String invalid) {
        Pedidos invalidClient = new Pedidos(null, invalid, List.of("product-1"), 10.0, "NEW");
        assertThatThrownBy(() -> service.save(invalidClient))
                .isInstanceOfAny(NullPointerException.class, IllegalArgumentException.class);

        Pedidos invalidStatus = new Pedidos(null, "client-1", List.of("product-1"), 10.0, invalid);
        assertThatThrownBy(() -> service.save(invalidStatus))
                .isInstanceOfAny(NullPointerException.class, IllegalArgumentException.class);

        Pedidos invalidProduct = new Pedidos(null, "client-1", Collections.singletonList(invalid), 10.0, "NEW");
        when(clientesRepository.existsById("client-1")).thenReturn(true);
        assertThatThrownBy(() -> service.save(invalidProduct))
                .isInstanceOfAny(NullPointerException.class, IllegalArgumentException.class);
    }

    @Test
    void shouldRejectEmptyOrNullProducts() {
        Pedidos emptyProducts = new Pedidos(null, "client-1", List.of(), 10.0, "NEW");
        Pedidos nullProducts = new Pedidos(null, "client-1", null, 10.0, "NEW");

        assertThatIllegalArgumentException().isThrownBy(() -> service.save(emptyProducts))
                .withMessage("produtosId não pode ser nulo ou vazio");
        assertThatIllegalArgumentException().isThrownBy(() -> service.save(nullProducts))
                .withMessage("produtosId não pode ser nulo ou vazio");
    }

    @Test
    void shouldRejectNullOrNegativeTotal() {
        Pedidos nullTotal = new Pedidos(null, "client-1", List.of("product-1"), null, "NEW");
        Pedidos negativeTotal = new Pedidos(null, "client-1", List.of("product-1"), -1.0, "NEW");

        assertThatIllegalArgumentException().isThrownBy(() -> service.save(nullTotal))
                .withMessage("valorTotal não pode ser nulo ou negativo");
        assertThatIllegalArgumentException().isThrownBy(() -> service.save(negativeTotal))
                .withMessage("valorTotal não pode ser nulo ou negativo");
    }

    @Test
    void shouldDeleteOrderWhenItExists() {
        when(pedidosRepository.findById("1")).thenReturn(Optional.of(order("1")));

        service.deleteById("1");

        verify(pedidosRepository).deleteById("1");
    }

    @Test
    void shouldNotDeleteMissingOrder() {
        when(pedidosRepository.findById("missing")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.deleteById("missing"))
                .isInstanceOf(ResourceNotFoundException.class);
        verify(pedidosRepository, never()).deleteById(anyString());
    }

    private static Pedidos order(String id) {
        return new Pedidos(id, "client-1", List.of("product-1"), 10.0, "NEW");
    }
}
