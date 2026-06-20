package ca.awoo.vidbox;

import jakarta.json.bind.Jsonb;
import jakarta.json.bind.JsonbBuilder;
import jakarta.websocket.EncodeException;
import jakarta.websocket.Encoder;

public class PlayerStateEncoder implements Encoder.Text<PlayerState> {

    @Override
    public String encode(PlayerState object) throws EncodeException {
        Jsonb jsonb = JsonbBuilder.create();
        return jsonb.toJson(object);
    }
    
}
