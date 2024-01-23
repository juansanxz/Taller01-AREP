/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package edu.escuelaing.arem.ASE.app;

import java.net.MalformedURLException;
import java.net.URL;

/**
 *
 * @author juan.sanchez-pe
 */
public class URLExplorer {
    public static void main( String[] args ) throws MalformedURLException
    {
        URL myUrl = new URL ("https://campusvirtual.escuelaing.edu.co:5678/moodle/ClientService.pdf?val=67&ang=6.30#grafico");
        System.out.println("Host: " + myUrl.getHost());
        System.out.println("Authority: " + myUrl.getAuthority());
        System.out.println("Path: " + myUrl.getPath());
        System.out.println("Protocol: " + myUrl.getProtocol());
        System.out.println("Port: " + myUrl.getPort());
        System.out.println("Query: " + myUrl.getQuery());
        System.out.println("Ref: " + myUrl.getRef());
        System.out.println("File: " + myUrl.getFile());
    }
    
    
}
