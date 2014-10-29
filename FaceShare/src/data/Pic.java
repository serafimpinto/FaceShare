package data;

import java.io.Serializable;

public class Pic implements Serializable{
	

	private static final long serialVersionUID = 1L;
	
	private String imagem;
	private String titulo;
	
	public Pic(String imagem, String titulo) {
		this.imagem = imagem;
		this.titulo = titulo;
	}
	
	public String getImagem() {
		return imagem;
	}
	public void setImagem(String imagem) {
		this.imagem = imagem;
	}
	public String getTitulo() {
		return titulo;
	}
	public void setTitulo(String titulo) {
		this.titulo = titulo;
	}
	
	public String toString() {
		return titulo;
	}

	public boolean equals(Object obj) {
		return ((Pic)obj).getImagem().equals(imagem);	
	}
}
