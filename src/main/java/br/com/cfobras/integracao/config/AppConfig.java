package br.com.cfobras.integracao.config;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class AppConfig {

    private final Properties prop = new Properties();

    public AppConfig() {
        carregarProperties();
    }

    private void carregarProperties() {
        if (tentarCarregar("dev.properties")) {
            return;
        }

        if (tentarCarregar("application.properties")) {
            return;
        }

        throw new RuntimeException("Nenhum arquivo de configuração encontrado (dev.properties ou application.properties) em src/main/resources");
    }

    private boolean tentarCarregar(String nomeArquivo) {
        try (InputStream in = AppConfig.class.getClassLoader().getResourceAsStream(nomeArquivo)) {

            if (in == null) {
                return false;
            }

            prop.load(in);
            return true;

        } catch (IOException e) {
            throw new RuntimeException("Erro ao carregar arquivo: " + nomeArquivo, e);
        }
    }

    public String getEmail() {
        String email = prop.getProperty("cfobras.login.email");
        if (email == null || email.isBlank()) {
            throw new RuntimeException("Config faltando: cfobras.login.email");
        }
        return email;
    }

    public String getSenha() {
        String senha = prop.getProperty("cfobras.login.senha");
        if (senha == null || senha.isBlank()) {
            throw new RuntimeException("Config faltando: cfobras.login.senha");
        }
        return senha;
    }
}
