/* Generated by the protocol buffer compiler.  DO NOT EDIT! */
/* Generated from: common/rwset.proto */

/* Do not generate deprecated warnings for self */
#ifndef PROTOBUF_C__NO_DEPRECATED
#define PROTOBUF_C__NO_DEPRECATED
#endif

#include "common/rwset.pb-c.h"
void   common__key_version__init
                     (Common__KeyVersion         *message)
{
  static const Common__KeyVersion init_value = COMMON__KEY_VERSION__INIT;
  *message = init_value;
}
size_t common__key_version__get_packed_size
                     (const Common__KeyVersion *message)
{
  assert(message->base.descriptor == &common__key_version__descriptor);
  return protobuf_c_message_get_packed_size ((const ProtobufCMessage*)(message));
}
size_t common__key_version__pack
                     (const Common__KeyVersion *message,
                      uint8_t       *out)
{
  assert(message->base.descriptor == &common__key_version__descriptor);
  return protobuf_c_message_pack ((const ProtobufCMessage*)message, out);
}
size_t common__key_version__pack_to_buffer
                     (const Common__KeyVersion *message,
                      ProtobufCBuffer *buffer)
{
  assert(message->base.descriptor == &common__key_version__descriptor);
  return protobuf_c_message_pack_to_buffer ((const ProtobufCMessage*)message, buffer);
}
Common__KeyVersion *
       common__key_version__unpack
                     (ProtobufCAllocator  *allocator,
                      size_t               len,
                      const uint8_t       *data)
{
  return (Common__KeyVersion *)
     protobuf_c_message_unpack (&common__key_version__descriptor,
                                allocator, len, data);
}
void   common__key_version__free_unpacked
                     (Common__KeyVersion *message,
                      ProtobufCAllocator *allocator)
{
  if(!message)
    return;
  assert(message->base.descriptor == &common__key_version__descriptor);
  protobuf_c_message_free_unpacked ((ProtobufCMessage*)message, allocator);
}
void   common__tx_read__init
                     (Common__TxRead         *message)
{
  static const Common__TxRead init_value = COMMON__TX_READ__INIT;
  *message = init_value;
}
size_t common__tx_read__get_packed_size
                     (const Common__TxRead *message)
{
  assert(message->base.descriptor == &common__tx_read__descriptor);
  return protobuf_c_message_get_packed_size ((const ProtobufCMessage*)(message));
}
size_t common__tx_read__pack
                     (const Common__TxRead *message,
                      uint8_t       *out)
{
  assert(message->base.descriptor == &common__tx_read__descriptor);
  return protobuf_c_message_pack ((const ProtobufCMessage*)message, out);
}
size_t common__tx_read__pack_to_buffer
                     (const Common__TxRead *message,
                      ProtobufCBuffer *buffer)
{
  assert(message->base.descriptor == &common__tx_read__descriptor);
  return protobuf_c_message_pack_to_buffer ((const ProtobufCMessage*)message, buffer);
}
Common__TxRead *
       common__tx_read__unpack
                     (ProtobufCAllocator  *allocator,
                      size_t               len,
                      const uint8_t       *data)
{
  return (Common__TxRead *)
     protobuf_c_message_unpack (&common__tx_read__descriptor,
                                allocator, len, data);
}
void   common__tx_read__free_unpacked
                     (Common__TxRead *message,
                      ProtobufCAllocator *allocator)
{
  if(!message)
    return;
  assert(message->base.descriptor == &common__tx_read__descriptor);
  protobuf_c_message_free_unpacked ((ProtobufCMessage*)message, allocator);
}
void   common__tx_write__init
                     (Common__TxWrite         *message)
{
  static const Common__TxWrite init_value = COMMON__TX_WRITE__INIT;
  *message = init_value;
}
size_t common__tx_write__get_packed_size
                     (const Common__TxWrite *message)
{
  assert(message->base.descriptor == &common__tx_write__descriptor);
  return protobuf_c_message_get_packed_size ((const ProtobufCMessage*)(message));
}
size_t common__tx_write__pack
                     (const Common__TxWrite *message,
                      uint8_t       *out)
{
  assert(message->base.descriptor == &common__tx_write__descriptor);
  return protobuf_c_message_pack ((const ProtobufCMessage*)message, out);
}
size_t common__tx_write__pack_to_buffer
                     (const Common__TxWrite *message,
                      ProtobufCBuffer *buffer)
{
  assert(message->base.descriptor == &common__tx_write__descriptor);
  return protobuf_c_message_pack_to_buffer ((const ProtobufCMessage*)message, buffer);
}
Common__TxWrite *
       common__tx_write__unpack
                     (ProtobufCAllocator  *allocator,
                      size_t               len,
                      const uint8_t       *data)
{
  return (Common__TxWrite *)
     protobuf_c_message_unpack (&common__tx_write__descriptor,
                                allocator, len, data);
}
void   common__tx_write__free_unpacked
                     (Common__TxWrite *message,
                      ProtobufCAllocator *allocator)
{
  if(!message)
    return;
  assert(message->base.descriptor == &common__tx_write__descriptor);
  protobuf_c_message_free_unpacked ((ProtobufCMessage*)message, allocator);
}
void   common__tx_rwset__init
                     (Common__TxRWSet         *message)
{
  static const Common__TxRWSet init_value = COMMON__TX_RWSET__INIT;
  *message = init_value;
}
size_t common__tx_rwset__get_packed_size
                     (const Common__TxRWSet *message)
{
  assert(message->base.descriptor == &common__tx_rwset__descriptor);
  return protobuf_c_message_get_packed_size ((const ProtobufCMessage*)(message));
}
size_t common__tx_rwset__pack
                     (const Common__TxRWSet *message,
                      uint8_t       *out)
{
  assert(message->base.descriptor == &common__tx_rwset__descriptor);
  return protobuf_c_message_pack ((const ProtobufCMessage*)message, out);
}
size_t common__tx_rwset__pack_to_buffer
                     (const Common__TxRWSet *message,
                      ProtobufCBuffer *buffer)
{
  assert(message->base.descriptor == &common__tx_rwset__descriptor);
  return protobuf_c_message_pack_to_buffer ((const ProtobufCMessage*)message, buffer);
}
Common__TxRWSet *
       common__tx_rwset__unpack
                     (ProtobufCAllocator  *allocator,
                      size_t               len,
                      const uint8_t       *data)
{
  return (Common__TxRWSet *)
     protobuf_c_message_unpack (&common__tx_rwset__descriptor,
                                allocator, len, data);
}
void   common__tx_rwset__free_unpacked
                     (Common__TxRWSet *message,
                      ProtobufCAllocator *allocator)
{
  if(!message)
    return;
  assert(message->base.descriptor == &common__tx_rwset__descriptor);
  protobuf_c_message_free_unpacked ((ProtobufCMessage*)message, allocator);
}
static const ProtobufCFieldDescriptor common__key_version__field_descriptors[2] =
{
  {
    "ref_tx_id",
    3,
    PROTOBUF_C_LABEL_NONE,
    PROTOBUF_C_TYPE_STRING,
    0,   /* quantifier_offset */
    offsetof(Common__KeyVersion, ref_tx_id),
    NULL,
    &protobuf_c_empty_string,
    0,             /* flags */
    0,NULL,NULL    /* reserved1,reserved2, etc */
  },
  {
    "ref_offset",
    4,
    PROTOBUF_C_LABEL_NONE,
    PROTOBUF_C_TYPE_INT32,
    0,   /* quantifier_offset */
    offsetof(Common__KeyVersion, ref_offset),
    NULL,
    NULL,
    0,             /* flags */
    0,NULL,NULL    /* reserved1,reserved2, etc */
  },
};
static const unsigned common__key_version__field_indices_by_name[] = {
  1,   /* field[1] = ref_offset */
  0,   /* field[0] = ref_tx_id */
};
static const ProtobufCIntRange common__key_version__number_ranges[1 + 1] =
{
  { 3, 0 },
  { 0, 2 }
};
const ProtobufCMessageDescriptor common__key_version__descriptor =
{
  PROTOBUF_C__MESSAGE_DESCRIPTOR_MAGIC,
  "common.KeyVersion",
  "KeyVersion",
  "Common__KeyVersion",
  "common",
  sizeof(Common__KeyVersion),
  2,
  common__key_version__field_descriptors,
  common__key_version__field_indices_by_name,
  1,  common__key_version__number_ranges,
  (ProtobufCMessageInit) common__key_version__init,
  NULL,NULL,NULL    /* reserved[123] */
};
static const ProtobufCFieldDescriptor common__tx_read__field_descriptors[4] =
{
  {
    "key",
    1,
    PROTOBUF_C_LABEL_NONE,
    PROTOBUF_C_TYPE_BYTES,
    0,   /* quantifier_offset */
    offsetof(Common__TxRead, key),
    NULL,
    NULL,
    0,             /* flags */
    0,NULL,NULL    /* reserved1,reserved2, etc */
  },
  {
    "value",
    2,
    PROTOBUF_C_LABEL_NONE,
    PROTOBUF_C_TYPE_BYTES,
    0,   /* quantifier_offset */
    offsetof(Common__TxRead, value),
    NULL,
    NULL,
    0,             /* flags */
    0,NULL,NULL    /* reserved1,reserved2, etc */
  },
  {
    "contract_name",
    3,
    PROTOBUF_C_LABEL_NONE,
    PROTOBUF_C_TYPE_STRING,
    0,   /* quantifier_offset */
    offsetof(Common__TxRead, contract_name),
    NULL,
    &protobuf_c_empty_string,
    0,             /* flags */
    0,NULL,NULL    /* reserved1,reserved2, etc */
  },
  {
    "version",
    4,
    PROTOBUF_C_LABEL_NONE,
    PROTOBUF_C_TYPE_MESSAGE,
    0,   /* quantifier_offset */
    offsetof(Common__TxRead, version),
    &common__key_version__descriptor,
    NULL,
    0,             /* flags */
    0,NULL,NULL    /* reserved1,reserved2, etc */
  },
};
static const unsigned common__tx_read__field_indices_by_name[] = {
  2,   /* field[2] = contract_name */
  0,   /* field[0] = key */
  1,   /* field[1] = value */
  3,   /* field[3] = version */
};
static const ProtobufCIntRange common__tx_read__number_ranges[1 + 1] =
{
  { 1, 0 },
  { 0, 4 }
};
const ProtobufCMessageDescriptor common__tx_read__descriptor =
{
  PROTOBUF_C__MESSAGE_DESCRIPTOR_MAGIC,
  "common.TxRead",
  "TxRead",
  "Common__TxRead",
  "common",
  sizeof(Common__TxRead),
  4,
  common__tx_read__field_descriptors,
  common__tx_read__field_indices_by_name,
  1,  common__tx_read__number_ranges,
  (ProtobufCMessageInit) common__tx_read__init,
  NULL,NULL,NULL    /* reserved[123] */
};
static const ProtobufCFieldDescriptor common__tx_write__field_descriptors[3] =
{
  {
    "key",
    1,
    PROTOBUF_C_LABEL_NONE,
    PROTOBUF_C_TYPE_BYTES,
    0,   /* quantifier_offset */
    offsetof(Common__TxWrite, key),
    NULL,
    NULL,
    0,             /* flags */
    0,NULL,NULL    /* reserved1,reserved2, etc */
  },
  {
    "value",
    2,
    PROTOBUF_C_LABEL_NONE,
    PROTOBUF_C_TYPE_BYTES,
    0,   /* quantifier_offset */
    offsetof(Common__TxWrite, value),
    NULL,
    NULL,
    0,             /* flags */
    0,NULL,NULL    /* reserved1,reserved2, etc */
  },
  {
    "contract_name",
    3,
    PROTOBUF_C_LABEL_NONE,
    PROTOBUF_C_TYPE_STRING,
    0,   /* quantifier_offset */
    offsetof(Common__TxWrite, contract_name),
    NULL,
    &protobuf_c_empty_string,
    0,             /* flags */
    0,NULL,NULL    /* reserved1,reserved2, etc */
  },
};
static const unsigned common__tx_write__field_indices_by_name[] = {
  2,   /* field[2] = contract_name */
  0,   /* field[0] = key */
  1,   /* field[1] = value */
};
static const ProtobufCIntRange common__tx_write__number_ranges[1 + 1] =
{
  { 1, 0 },
  { 0, 3 }
};
const ProtobufCMessageDescriptor common__tx_write__descriptor =
{
  PROTOBUF_C__MESSAGE_DESCRIPTOR_MAGIC,
  "common.TxWrite",
  "TxWrite",
  "Common__TxWrite",
  "common",
  sizeof(Common__TxWrite),
  3,
  common__tx_write__field_descriptors,
  common__tx_write__field_indices_by_name,
  1,  common__tx_write__number_ranges,
  (ProtobufCMessageInit) common__tx_write__init,
  NULL,NULL,NULL    /* reserved[123] */
};
static const ProtobufCFieldDescriptor common__tx_rwset__field_descriptors[3] =
{
  {
    "tx_id",
    1,
    PROTOBUF_C_LABEL_NONE,
    PROTOBUF_C_TYPE_STRING,
    0,   /* quantifier_offset */
    offsetof(Common__TxRWSet, tx_id),
    NULL,
    &protobuf_c_empty_string,
    0,             /* flags */
    0,NULL,NULL    /* reserved1,reserved2, etc */
  },
  {
    "tx_reads",
    2,
    PROTOBUF_C_LABEL_REPEATED,
    PROTOBUF_C_TYPE_MESSAGE,
    offsetof(Common__TxRWSet, n_tx_reads),
    offsetof(Common__TxRWSet, tx_reads),
    &common__tx_read__descriptor,
    NULL,
    0,             /* flags */
    0,NULL,NULL    /* reserved1,reserved2, etc */
  },
  {
    "tx_writes",
    3,
    PROTOBUF_C_LABEL_REPEATED,
    PROTOBUF_C_TYPE_MESSAGE,
    offsetof(Common__TxRWSet, n_tx_writes),
    offsetof(Common__TxRWSet, tx_writes),
    &common__tx_write__descriptor,
    NULL,
    0,             /* flags */
    0,NULL,NULL    /* reserved1,reserved2, etc */
  },
};
static const unsigned common__tx_rwset__field_indices_by_name[] = {
  0,   /* field[0] = tx_id */
  1,   /* field[1] = tx_reads */
  2,   /* field[2] = tx_writes */
};
static const ProtobufCIntRange common__tx_rwset__number_ranges[1 + 1] =
{
  { 1, 0 },
  { 0, 3 }
};
const ProtobufCMessageDescriptor common__tx_rwset__descriptor =
{
  PROTOBUF_C__MESSAGE_DESCRIPTOR_MAGIC,
  "common.TxRWSet",
  "TxRWSet",
  "Common__TxRWSet",
  "common",
  sizeof(Common__TxRWSet),
  3,
  common__tx_rwset__field_descriptors,
  common__tx_rwset__field_indices_by_name,
  1,  common__tx_rwset__number_ranges,
  (ProtobufCMessageInit) common__tx_rwset__init,
  NULL,NULL,NULL    /* reserved[123] */
};
