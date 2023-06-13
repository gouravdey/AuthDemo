package com.gd.democlient;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.*;
import java.security.cert.CertificateException;

import static com.gd.democlient.utils.SecurityUtils.createClientAssertion;

@SpringBootApplication
public class DemoClientApplication {
	public static void main(String[] args) {
		SpringApplication.run(DemoClientApplication.class, args);
	}
}
