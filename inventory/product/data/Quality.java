package farm.inventory.product.data;

/**
 * Quality ratings given to products in the farm's inventory.
 * <p>
 * Awarded to products on an individual basis, such that products of the same type may have distinct
 * quality levels (i.e. in the same inventory, one egg may be ranked silver and another iridium).
 * Declared in ascending order of quality. 
 * </p>
 * <p>
 * <i>If this seems <a href="https://www.stardewvalley.net/" target="_blank">familiar</a> to you... 
 * shush, no it doesn't.</i> 
 * </p>
 * @provided
 */
public enum Quality {
    REGULAR,
    SILVER,
    GOLD, 
    IRIDIUM
}
