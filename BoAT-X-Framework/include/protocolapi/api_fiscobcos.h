/******************************************************************************
 * Copyright (C) 2018-2021 aitos.io
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *****************************************************************************/

/*!@brief BoAT IoT SDK interface header file for FISCOBCOS

@file
api_fiscobcos.h is header file for BoAT IoT SDK FISCOBCOS's interface.
*/

#ifndef __API_FISCOBCOS_H__
#define __API_FISCOBCOS_H__

#include "boatiotsdk.h"

/*! @defgroup fiscobcos-api boat fiscobcos-API
 * @{
 */


#define BOAT_FISCOBCOS_MINE_INTERVAL             3  //!< Mining Interval of the blockchain, in seconds
#define BOAT_FISCOBCOS_WAIT_PENDING_TX_TIMEOUT   30 //!< Timeout waiting for a transaction being mined, in seconds

#define BOAT_FISCOBCOS_NODE_URL_MAX_LEN          127
#define BOAT_FISCOBCOS_NONCE_AUTO                0xFFFFFFFFFFFFFFFF
#define BOAT_FISCOBCOS_ADDRESS_SIZE              20


//!@brief Account information
//! An account's only identifier is its private key. An address is calculated
//! from the public key and the public key is calculated from the private key.
typedef struct TBoatFiscobcosAccountInfo
{
    BoatKeypairPriKeyCtx prikeyCtx;         //!< prikey context                       

    BUINT8  address[BOAT_FISCOBCOS_ADDRESS_SIZE];//!< Account address calculated from public key
}BoatFiscobcosAccountInfo;



//!@brief Wallet information

//! Wallet information consists of account and block chain network information.
//! Currently only one account per wallet is supported.
//! NOTE: Members  of account_info and network_info will be stored, DO NOT put pointer member in it
typedef struct TBoatFiscobcosWallet
{
    BoatFiscobcosAccountInfo account_info; //!< Account information
    BoatFiscobcosNetworkData network_info; //!< Network information

    // Fiscobcos wallet internal members. DO NOT access them from outside wallet protocol.
    struct TWeb3IntfContext *web3intf_context_ptr;  //!< Web3 Interface Context
}BoatFiscobcosWallet;


//!@brief ECDSA signature struct
typedef struct TBoatFiscobcosTxFieldSig
{
    union
    {
        struct
        {
            BUINT8 r32B[32]; //!< r part of the signature
            BUINT8 s32B[32]; //!< s part of the signature
        };
        BUINT8 sig64B[64];   //!< consecutive signature composed of r+s
    };
    BUINT8 r_len;            //!< Effective length of r, either 0 for unsigned tx and 32 for signed tx
    BUINT8 s_len;            //!< Effective length of s, either 0 for unsigned tx and 32 for signed tx
}BoatFiscobcosTxFieldSig;

//!@brief RAW FISCO BOCS transaction fields

//! The difference from Fiscobcos is appending blocklimit, chainid and groupid field
//! at the end of the struct. Thus a BoatFiscobcosRawtxFields pointer can be transfered 
//! to any function that accepts a BoatEthRawtxFields argument. It behaves as a C 
//! implementation of class inheritance, i.e. a pointer of an inherited class could 
//! be assigned to a pointer of a base class.
typedef struct TBoatFiscobcosRawtxFields
{
    // Following fields are inherited from Fiscobcos.
    // DO NOT modify these fields unless all of Fiscobcos and all other protocols
    // inherited from Fiscobcos are modified synchronously.
    BoatFieldMax32B nonce;        //!< nonce, uint256 in bigendian
    BoatFieldMax32B gasprice;     //!< gasprice in wei, uint256 in bigendian
    BoatFieldMax32B gaslimit;     //!< gaslimit, uint256 in bigendian
    BUINT8 recipient[BOAT_FISCOBCOS_ADDRESS_SIZE]; //!< recipient's address, 160 bits
    BoatFieldMax32B value;        //!< value to transfer, uint256 in bigendian
    BoatFieldVariable data;       //!< data to transfer, unformatted stream
    BoatFieldMax4B v;             //!< chain id or recovery identifier, @see RawtxPerform()
    BoatFiscobcosTxFieldSig sig;        //!< ECDSA signature, including r and s parts
    
    // FISCOBCOS specific fields are appended here.
    BoatFieldMax32B blocklimit;   //!< transaction life cycle
	BoatFieldMax32B chainid;      //!< record the chain/business information to which the transaction belongs
	BoatFieldMax32B groupid;      //!< group to which this transaction belongs
	BoatFieldVariable extraData;  //!< reserved segment
}BoatFiscobcosRawtxFields;

//!@brief FISCOBCOS Transaction

//! The only difference between FISCO BCOS transaction and Fiscobcos transaction is
//! BoatFiscobcosRawtxFields has more fields than BoatEthRawtxFields. To allow
//! FISCO BCOS to re-use Ethereum APIs that take BoatEthTx as function arguments,
//! <rawtx_fields> MUST be the last field.
typedef struct TBoatFiscobcosTx
{
    BoatFiscobcosWallet *wallet_ptr; //!< Wallet pointer the transaction is combined with
									 //!< @note: in fiscobcos, the member chain_id and eip155_compatibility is UNUSED,
									 //!< fiscobcos is NOT compliance with EIP155.
    BoatFieldMax32B tx_hash;         //!< Transaction hash returned from network
    BBOOL is_sync_tx;                //!< True to perform a synchronous transaction (wait for getting mined), False for asynchronous transaction

    // rawtx_fields MUST be the last field
    BoatFiscobcosRawtxFields rawtx_fields; //!< RAW transaction fields
}BoatFiscobcosTx;


#ifdef __cplusplus
extern "C" {
#endif


/*!****************************************************************************
 * @brief Initialize a transaction
 * @see BoatFiscobcosTxInit()
 ******************************************************************************/
BOAT_RESULT BoatFiscobcosTxInit(BoatFiscobcosWallet *wallet_ptr,
								BoatFiscobcosTx *tx_ptr,
								BBOOL is_sync_tx,
								BCHAR *gasprice_str,
								BCHAR *gaslimit_str,
								BCHAR *recipient_str,
								BCHAR *chainid_str,
								BCHAR *groupid_str);


/*!****************************************************************************
 * @brief Set Nonce
 * @see BoatFiscobcosTxSetNonce()
 ******************************************************************************/
BOAT_RESULT BoatFiscobcosTxSetNonce(BoatFiscobcosTx *tx_ptr, BUINT64 nonce);


/*!****************************************************************************
 * @brief Construct a raw FISCO BCOS transaction synchronously.
 *
 * @details
 *   This function is similar to FiscobcosSendRawtx except that it waits for the
 *   transaction being mined.
 *	 
 * @param[in] tx_ptr
 *   A pointer to the context of the transaction.
 *
 * @return
 *   This function returns BOAT_SUCCESS if successful. Otherwise it returns one\n
 *   of the error codes.
 *	 
 * @see FiscobcosSendRawtx()
 ******************************************************************************/
BOAT_RESULT BoatFiscobcosSendRawtxWithReceipt(BOAT_INOUT BoatFiscobcosTx *tx_ptr);

/*!****************************************************************************
 * @brief Sign and send a transaction. Also call a stateful contract function.
 *
 * @details
 *   This function sign and set a transaction.
 *   \n BoatWalletSetXXX() and BoatTxSetXXX() functions must be properly called
 *   before call this function.
 *   \n A transaction whose recipient may be an EOA address or a contract address.
 *   In latter case it's usually a contract function call.
 *   \n This function invokes the eth_sendRawTransaction RPC method.
 *   eth_sendRawTransaction method only applies the transaction and returns a
 *   transaction hash. The transaction is not verified (got mined) until the
 *   nodes in the network get into consensus about the transaction. This
 *   function will invoke eth_getTransactionReceipt method to wait for the
 *   transaction being mined or timeout.
 *   \n If the transaction is a contract function call, the caller cannot get its
 *   return value because the transaction is asynchronously executed. It's a
 *   good practice to save the return value in a state variable and use
 *   BoatFiscobcosCallContractFunc() to call a "read" contract function that could read
 *   and return the state variable.
 *
 * @note
 *   Any contract function that may change the state of the contract shall
 *   be called in a transaction way. "state" is the "global variable" used in a
 *   contract.
 *   \n Any contract function that doesn't change the state of the contract can
 *   be called either in a transaction way or by BoatFiscobcosCallContractFunc(), which
 *   invokes the eth_call RPC method. However the former will consume gas and
 *   latter doesn't consume gas.
 *
 * @param[in] tx_ptr
 *   Pointer to the transaction structure.
 *   
 * @return
 *   This function returns BOAT_SUCCESS if setting is successful.\n
 *   Otherwise it returns one of the error codes.
 *
 * @see BoatFiscobcosTxSend()
 ******************************************************************************/
BOAT_RESULT BoatFiscobcosTxSend(BoatFiscobcosTx *tx_ptr);


/*!****************************************************************************
 * @brief Call a state-less contract function
 * @see BoatFiscobcosCallContractFunc()
 ******************************************************************************/
BCHAR *BoatFiscobcosCallContractFunc(BoatFiscobcosTx *tx_ptr, BCHAR *func_proto_str, 
									 BUINT8 *rlp_param_ptr, BUINT32 rlp_param_len);


/*!****************************************************************************
 * @brief Wait for a transaction being mined.
* @see BoatFiscobcosGetTransactionReceipt()
 ******************************************************************************/
BOAT_RESULT BoatFiscobcosGetTransactionReceipt(BoatFiscobcosTx *tx_ptr);

/*!****************************************************************************
 * @brief query current group block number.
 ******************************************************************************/
BCHAR *BoatFiscobcosGetBlockNumber(BoatFiscobcosTx *tx_ptr);

// Ethereum APIs compatible for FISCOBCOS

/*!****************************************************************************
 * @brief Initialize Boat FISCOBCOS Wallet
 * @see BoatFiscobcosWalletInit()
 ******************************************************************************/
 BoatFiscobcosWallet *BoatFiscobcosWalletInit(BUINT8 walletIndex,BUINT8 accountIndex);


/*!****************************************************************************
 * @brief De-initialize Boat FISCOBCOS Wallet
 * @see BoatFiscobcosWalletDeInit()
 ******************************************************************************/
void BoatFiscobcosWalletDeInit(BoatFiscobcosWallet *wallet_ptr);

/*!*****************************************************************************
* @brief Parse RPC method RESPONSE
* @see eth_parse_json_result()
*******************************************************************************/
BOAT_RESULT fiscobcos_parse_json_result(const BCHAR *json_string, 
								                                  const BCHAR *child_name, 
								                                  BoatFieldVariable *result_out);


/*!****************************************************************************
 * @brief Set GasPrice
 * @see BoatEthTxSetGasPrice()
 ******************************************************************************/
BOAT_RESULT BoatFiscobcosTxSetGasPrice(BoatFiscobcosTx *tx_ptr, BoatFieldMax32B *gas_price_ptr);


/*!****************************************************************************
 * @brief Set GasLimit
 * @see BoatEthTxSetGasLimit()
 ******************************************************************************/
BOAT_RESULT BoatFiscobcosTxSetGasLimit(BoatFiscobcosTx *tx_ptr, BoatFieldMax32B *gas_limit_ptr);


/*!****************************************************************************
 * @brief Set Recipient
 * @see BoatEthTxSetRecipient()
 ******************************************************************************/
BOAT_RESULT BoatFiscobcosTxSetRecipient(BoatFiscobcosTx *tx_ptr, BUINT8 address[BOAT_FISCOBCOS_ADDRESS_SIZE]);


/*!*****************************************************************************
 * @brief Set Value
 * @see BoatEthTxSetValue()
 *******************************************************************************/
BOAT_RESULT BoatFiscobcosTxSetValue(BoatFiscobcosTx *tx_ptr, BoatFieldMax32B *value_ptr);


/*!****************************************************************************
 * @brief Set Data
 * @see BoatEthTxSetData()
 ******************************************************************************/
BOAT_RESULT BoatFiscobcosTxSetData(BoatFiscobcosTx *tx_ptr, BoatFieldVariable *data_ptr);

/*!****************************************************************************
 * @brief Parse RPC method RESPONSE.
 * @see eth_parse_json_result()
 ******************************************************************************/
BOAT_RESULT BoatFiscobcosParseRpcResponseStringResult(const BCHAR *json_string, BoatFieldVariable *result_out);

/*!****************************************************************************
 * @brief Parse RPC method RESPONSE.
 * @see eth_parse_json_result()
 ******************************************************************************/
BOAT_RESULT BoatFiscobcosParseRpcResponseResult(const BCHAR *json_string, 
                                                const BCHAR *child_name, 
                                                BoatFieldVariable *result_out);

/*! @}*/

#ifdef __cplusplus
}
#endif /* end of __cplusplus */

#endif