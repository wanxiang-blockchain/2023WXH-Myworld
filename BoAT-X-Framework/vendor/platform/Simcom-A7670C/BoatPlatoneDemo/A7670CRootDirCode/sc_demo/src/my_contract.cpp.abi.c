/******************************************************************************
This file is generated from contract ABI. DO NOT modify it by hand.
******************************************************************************/


// Generated C function interface from smart contract ABI

#include "boatiotsdk.h"
#include "api_platone.h"

BCHAR * my_contract_cpp_abi_setName(BoatPlatoneTx *tx_ptr, BCHAR* msg)
{
    static BCHAR tx_hash_str[67] = "";
    BoatFieldVariable data_field;
    RlpEncodedStreamObject * rlp_stream_ptr;
    RlpObject rlp_object_list;
    RlpObject rlp_object_txtype;
    BUINT64 txtype;
    RlpObject rlp_object_string_func_name;
    RlpObject rlp_object_string_msg;
    boat_try_declare;

    boat_try(BoatPlatoneTxSetNonce(tx_ptr, BOAT_PLATONE_NONCE_AUTO));

    boat_try(RlpInitListObject(&rlp_object_list));

    txtype = tx_ptr->rawtx_fields.txtype;
    boat_try(RlpInitStringObject(&rlp_object_txtype, UtilityChangeEndian(&txtype, sizeof(txtype)), sizeof(txtype)));
    boat_try(0 > RlpEncoderAppendObjectToList(&rlp_object_list, &rlp_object_txtype));

    boat_try(RlpInitStringObject(&rlp_object_string_func_name, (BUINT8*)"setName", strlen("setName")));
    boat_try(0 > RlpEncoderAppendObjectToList(&rlp_object_list, &rlp_object_string_func_name));

    boat_try(RlpInitStringObject(&rlp_object_string_msg, (BUINT8*)msg, strlen(msg)));
    boat_try(0 > RlpEncoderAppendObjectToList(&rlp_object_list, &rlp_object_string_msg));

    boat_try(RlpEncode(&rlp_object_list, NULL));

    rlp_stream_ptr = RlpGetEncodedStream(&rlp_object_list);

    data_field.field_ptr = rlp_stream_ptr->stream_ptr;
    data_field.field_len = rlp_stream_ptr->stream_len;
    boat_try(BoatPlatoneTxSetData(tx_ptr, &data_field));

    boat_try(BoatPlatoneTxSend(tx_ptr));

    UtilityHexToBin((BUINT8 *)tx_hash_str, tx_ptr->tx_hash.field_len, (BCHAR *)tx_ptr->tx_hash.field,TRIMBIN_TRIM_NO, BOAT_FALSE);


    boat_catch(cleanup)
    {
        //BoatLog(BOAT_LOG_VERBOSE, "Exception: %d", boat_exception);
        RlpRecursiveDeleteObject(&rlp_object_list);
        return(NULL);
    }
        
    RlpRecursiveDeleteObject(&rlp_object_list);
    return(tx_hash_str);

}

BCHAR * my_contract_cpp_abi_getName(BoatPlatoneTx *tx_ptr)
{
    BCHAR *call_result_str = NULL;
    RlpEncodedStreamObject * rlp_stream_ptr;
    RlpObject rlp_object_list;
    RlpObject rlp_object_txtype;
    BUINT64 txtype;
    RlpObject rlp_object_string_func_name;
    boat_try_declare;

    boat_try(RlpInitListObject(&rlp_object_list));

    txtype = tx_ptr->rawtx_fields.txtype;
    boat_try(RlpInitStringObject(&rlp_object_txtype, UtilityChangeEndian(&txtype, sizeof(txtype)), sizeof(txtype)));
    boat_try(0 > RlpEncoderAppendObjectToList(&rlp_object_list, &rlp_object_txtype));

    boat_try(RlpInitStringObject(&rlp_object_string_func_name, (BUINT8*)"getName", strlen("getName")));
    boat_try(0 > RlpEncoderAppendObjectToList(&rlp_object_list, &rlp_object_string_func_name));

    boat_try(RlpEncode(&rlp_object_list, NULL));

    rlp_stream_ptr = RlpGetEncodedStream(&rlp_object_list);

    call_result_str = BoatPlatoneCallContractFunc(tx_ptr, rlp_stream_ptr->stream_ptr, rlp_stream_ptr->stream_len);


    boat_catch(cleanup)
    {
       // BoatLog(BOAT_LOG_VERBOSE, "Exception: %d", boat_exception);
        RlpRecursiveDeleteObject(&rlp_object_list);
        return(NULL);
    }
        
    RlpRecursiveDeleteObject(&rlp_object_list);
    return(call_result_str);

}

