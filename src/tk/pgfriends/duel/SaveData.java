package tk.pgfriends.duel;

import java.io.File;
import java.io.IOException;
import java.util.UUID;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

public class SaveData {
	
	
	
	public static void save(UUID uuid, PlayerInventory PI) {
		
		File file1 = new File(Main.plugin.getDataFolder() + File.separator + uuid.toString()); // Creates a File object
		FileConfiguration database1 = YamlConfiguration.loadConfiguration(file1); // Turns the File object into YAML and loads data
		
		
		
		
		
		
		int recursion = 0;
		String bigChungaloid = "";
		for (ItemStack item : PI.getContents()) {
			bigChungaloid.concat(String.valueOf(recursion) + " : " + item.serialize().toString());
		}
		
		
		
		
		
		database1.set(uuid.toString(), bigChungaloid);
		
		try {
			database1.save(file1);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
	}
	
	
	
	
	
	
	
	
	
	
}
