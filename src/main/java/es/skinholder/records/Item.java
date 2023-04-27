package es.skinholder.records;

/**
 * @author jagobainda
 * La clase Item representa un artículo con un enlace y una cantidad.
 *
 * @param ENLACE la URL o enlace del artículo
 * @param CANTIDAD la cantidad de artículos que se desean
 */
public record Item(String ENLACE, int CANTIDAD) {
}
