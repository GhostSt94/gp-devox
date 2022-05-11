import { createStore } from 'vuex'
import axios from "axios";
import clients from "./modules/clients";

export default createStore({
    state:{
        API_URL:"http://localhost:8888/",
    },
    getters:{
        getApiUrl:(state)=>state.API_URL,
    },
    actions:{
        async logout({getters}){
            let url=getters.getApiUrl
            await axios.get(`${url}/auth/logout`);
        }
    },
    modules:{clients}
})



