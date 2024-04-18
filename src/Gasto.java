record Gasto(String descripcion, double monto) {

    @Override
    public String toString() {
        return "- Descripción: " + descripcion + ", Monto: $" + monto;
    }
}
