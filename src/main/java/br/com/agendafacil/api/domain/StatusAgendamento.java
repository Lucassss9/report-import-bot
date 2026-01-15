package br.com.agendafacil.api.domain;

import lombok.Getter;

@Getter
public enum StatusAgendamento {

    PENDENTE("Pendente"),
    CONFIRMADO("Confirmado"),
    CONCLUIDO("Concluido"),
    CANCELADO("Cancelado"),
    NAO_COMPARECEU("Nao Compareceu");

    private final String status;

    StatusAgendamento(String status){
        this.status = status;
    }

}
